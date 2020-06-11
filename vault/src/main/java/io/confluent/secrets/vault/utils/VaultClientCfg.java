/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.vault.utils;

import org.apache.kafka.common.config.AbstractConfig;

public class VaultClientCfg {
    public static final String VAULT_URL_KEY = "url";
    public static final String VAULT_TOKEN_KEY =  "token";
    public static final String KEYSTORE =  "keystore.location";
    public static final String KEYSTORE_PASSWORD =  "keystore.password";
    public static final String TRUSTSTORE =  "truststore.location";
    public static final String RETRIES =  "maxRetries";
    public static final String INTERVAL =  "maxInterval";
    public static final String VERSION =  "version";
    public static final String PROVIDER_PATH_KEY = "path";

    private final String url;
    private final String keystore;
    private final String keystorePassword;
    private final String truststore;
    private final int maxRetries;
    private final int maxInterval;
    private final int version;
    private String path;
    private final String token;

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public String getKeystore() {
        return keystore;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getTruststore() {
        return truststore;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getMaxInterval() {
        return maxInterval;
    }

    public int getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public String getToken() {
        return token;
    }

    public VaultClientCfg(String url,
                          String keystore,
                          String keystorePassword,
                          String truststore,
                          int maxRetries,
                          int maxInterval,
                          int version,
                          String token) {
        this.url = url;
        this.keystore = keystore;
        this.keystorePassword = keystorePassword;
        this.truststore = truststore;
        this.maxRetries = maxRetries;
        this.maxInterval = maxInterval;
        this.version = version;
        this.token = token;
    }

    public VaultClientCfg(String url,
                          String keystore,
                          String keystorePassword,
                          String truststore,
                          int maxRetries,
                          int maxInterval,
                          int version,
                          String path,
                          String token) {
        this(url, keystore, keystorePassword, truststore, maxRetries, maxInterval, version, token);
        this.path = path;
    }

    public static VaultClientCfg getClientCfg(final String rootKey, final AbstractConfig config) {
        return new VaultClientCfg(
                config.getString(rootKey + VAULT_URL_KEY),
                config.getString(rootKey + KEYSTORE),
                config.getString(rootKey + KEYSTORE_PASSWORD),
                config.getString(rootKey + TRUSTSTORE),
                config.getInt(rootKey + RETRIES),
                config.getInt(rootKey + INTERVAL),
                config.getInt(rootKey + VERSION),
                config.getString(rootKey + PROVIDER_PATH_KEY),
                config.getPassword(rootKey + VAULT_TOKEN_KEY).value());
    }

    public static VaultClientCfg getClientCfg(final AbstractConfig config) {
        return new VaultClientCfg(
                config.getString(VAULT_URL_KEY),
                config.getString(KEYSTORE),
                config.getString(KEYSTORE_PASSWORD),
                config.getString(TRUSTSTORE),
                config.getInt(RETRIES),
                config.getInt(INTERVAL),
                config.getInt(VERSION),
                config.getPassword(VAULT_TOKEN_KEY).value());
    }
}
