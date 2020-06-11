/*
 * Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.cyberark;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.secured.common.secretprovider.impl.AbstractSecretsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class CyberarkSecretProvider extends AbstractSecretsProvider<CyberarkSecretProviderCfg>  {
    private final static Logger LOGGER = LoggerFactory.getLogger(CyberarkSecretProvider.class);

    private String appID;
    private String url;

    private final Map<String, Map<String, String>> secrets = new HashMap<>();

    @Override
    public String getSecret(String safe, String object) {
        return secrets.computeIfAbsent(safe, (key) -> new HashMap<>())
                .computeIfAbsent(object, (key) -> {
                   synchronized (this) {
                       // Execute a GET with timeout settings and return response content as String.
                       try {
                           // SSL Layer
                           final SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
                                   SSLContextBuilder
                                           .create()
                                           .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                           .build());

                           final CloseableHttpClient httpclient = HttpClients
                                   .custom()
                                   .setSSLSocketFactory(factory)
                                   .build();

                           final String endpoint = String.format("%s/AIMWebService/api/Accounts/?UserName=%s&AppID=%s&Safe=%s",
                                   url,
                                   URLEncoder.encode(object, StandardCharsets.UTF_8.toString()),
                                   appID,
                                   safe);

                           final HttpGet httpGet = new HttpGet(endpoint);
                           final CloseableHttpResponse response = httpclient.execute(httpGet);

                           if (response.getStatusLine().getStatusCode() == 200) {
                               final CyberarkSecret secret = convertToSecret(response);
                               return secret.Content;
                           }

                           final CyberarkError error = convertToError(response);
                           throw new RuntimeException(error.ErrorMsg);
                       } catch (KeyManagementException | IOException | NoSuchAlgorithmException | KeyStoreException e) {
                           LOGGER.error("Error requesting password for object '{}'.", object, e);
                           throw new RuntimeException(e);
                       }
                   }
                });
    }

    @Override
    public CyberarkSecretProviderCfg getConfig(Map<String, ?> properties) {
        return new CyberarkSecretProviderCfg(properties);
    }

    @Override
    public void configure(CyberarkSecretProviderCfg configuration) {
        appID = configuration.getString(CyberarkSecretProviderCfg.APPID);
        url = configuration.getString(CyberarkSecretProviderCfg.HOST);
    }

    private CyberarkError convertToError(final CloseableHttpResponse response) throws IOException {
        return new ObjectMapper().readValue(convert(response, StandardCharsets.UTF_8), CyberarkError.class);
    }

    private CyberarkSecret convertToSecret(final CloseableHttpResponse response) throws IOException {
        return  new ObjectMapper().readValue(convert(response, StandardCharsets.UTF_8), CyberarkSecret.class);
    }

    private String convert(CloseableHttpResponse response, Charset charset) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response
                        .getEntity()
                        .getContent(), charset))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }
}

// https://services-uscentral.skytap.com:18571/AIMWebService/api/Accounts/?Object=Operating%20System-UnixSSH-192.168.0.1-testPascal&AppID=ConfluentKafka&Safe=Test
// https://services-uscentral.skytap.com:18571/AIMWebService/api/Accounts/?Object=Operating%20System-UnixSSH-192.168.0.1-testPascal&AppID=ConfluentKafka&Safe=Test