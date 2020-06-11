/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.aws;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.secured.common.secretprovider.SubscriptionHandler;
import io.confluent.kafka.secured.common.secretprovider.impl.AbstractSecretsProvider;
import io.confluent.kafka.secured.common.secretprovider.impl.PollingSubscriptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AWSSecretProvider  extends AbstractSecretsProvider<AWSSecretProviderCfg> implements PollingSubscriptionHandler.Supplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSSecretProvider.class);

    private String region;
    private AWSSecretsManager client;

    private Map<String, Map<String, String>> secrets = new ConcurrentHashMap<>();

    @Override
    public AWSSecretProviderCfg getConfig(Map<String, ?> properties) {
        return new AWSSecretProviderCfg(properties);
    }

    @Override
    public SubscriptionHandler getSubscriptionHandler(AWSSecretProviderCfg cfg) {
        return new PollingSubscriptionHandler(cfg, this);
    }

    @Override
    public void configure(AWSSecretProviderCfg cfg) {
        this.region = cfg.getString(AWSSecretProviderCfg.REGION);
    }

    @Override
    public Map<String, String> getSecrets(final String storeName) {
        final Map<String, String> values = loadSecrets(storeName);

        secrets.put(storeName, values);

        return values;
    }

    @Override
    public String getSecret(final String storeName, final String keyName) {
        final Map<String, String> values = secrets.computeIfAbsent(storeName, (k) -> loadSecrets(storeName));

        return values.get(keyName);
    }

    protected AWSSecretsManager getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = (StringUtils.isEmpty(region))
                            ? AWSSecretsManagerClientBuilder.standard().build()
                            : AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
                }
            }
        }

        return client;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> loadSecrets(final String storeName) {
        try {
            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(storeName);

            GetSecretValueResult getSecretValueResult = getClient().getSecretValue(getSecretValueRequest);

            final String serializedSecrets = getSecretValueResult.getSecretString();
            if (StringUtils.isEmpty(serializedSecrets)) {
                LOGGER.warn("No secrets returned or binary secrets are not supported yet.");
                return new HashMap<>();
            }

            final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
            return  (Map<String, String>) new ObjectMapper().readValue(serializedSecrets, typeRef);
        } catch(ResourceNotFoundException e) {
            LOGGER.error("The requested secret '{}' was not found.", storeName, e);
            throw e;
        } catch (InvalidRequestException e) {
            LOGGER.error("The request was invalid due to: ", e);
            throw e;
        } catch (InvalidParameterException e) {
            LOGGER.error("The request had invalid params: ", e);
            throw e;
        } catch (IOException e) {
            LOGGER.error("Error deserializing secrets.", e);
            throw new RuntimeException(e);
        }
    }
}
