/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.vault.utils;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class VaultClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaultClient.class);

    private Vault vault;

    private final Cache<String, Map<String, String>> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(60*60, TimeUnit.SECONDS)
            .build();

    VaultClient(final VaultClientCfg clientCfg) throws VaultException {
        // Adding key properties
        final String keystore = clientCfg.getKeystore();
        final String keystorePassword = clientCfg.getKeystorePassword();
        final String truststore = clientCfg.getTruststore();
        final int maxRetries = clientCfg.getMaxRetries();         // Retry 3 times
        final int maxInterval = clientCfg.getMaxInterval();    // 1 sec interval
        final int version = clientCfg.getVersion();    // Default V1

        try {
            // SSL Configuration
            final SslConfig sslConfig = (StringUtils.isNotEmpty(truststore) || StringUtils.isNotEmpty(keystore))
                    ? new SslConfig()
                        .keyStoreFile(new File(keystore), keystorePassword)
                        .trustStoreFile(new File(truststore))
                    : null;

            // Create Vault client
            VaultConfig config = new VaultConfig()
                    .engineVersion(version)
                    .address(clientCfg.getUrl())
                    .token(clientCfg.getToken())
                    .sslConfig(sslConfig)
                    .build();

            vault = new Vault(config)
                    .withRetries(maxRetries, maxInterval);
        } catch (VaultException e) {
            LOGGER.error("Error creating Vault client '{}'.", clientCfg.getUrl(), e);

            throw e;
        }
    }

    public Map<String, String> getSecrets(final String vaultSecretPath, final boolean invalidateCache) {
        try {
            if (invalidateCache) {
                cache.invalidate(vaultSecretPath);
            }

            return cache.get(vaultSecretPath, () -> getSecretsMap(vaultSecretPath));
        } catch (ExecutionException e) {
            LOGGER.error("Error retrieving secrets in store '{}'", vaultSecretPath, e);
            return null;
        }
    }

    public String getSecret(final String key, final String vaultSecretPath) {
        try {
            final Map<String, String> secrets = cache.get(vaultSecretPath, () -> getSecretsMap(vaultSecretPath));

            return secrets.get(key);
        } catch (ExecutionException e) {
            LOGGER.error("Error retrieving secret '{}' in store '{}'", key, vaultSecretPath, e);
            return null;
        }
    }

    public void addSecret(final String key, final String value, final String vaultSecretPath) {
        try {
            final Map<String, String> secrets = cache.get(vaultSecretPath, () -> getSecretsMap(vaultSecretPath));

            secrets.put(key, value);

            vault.logical().write(vaultSecretPath, Collections.unmodifiableMap(secrets));
            cache.put(vaultSecretPath, secrets);

        } catch (ExecutionException | VaultException e) {
            LOGGER.error("Error updating secret '{}' in store '{}'", key, vaultSecretPath, e);
        }
    }

    private Map<String, String> getSecretsMap(final String vaultSecretPath) {
        try {
            return vault.logical()
                    .read(vaultSecretPath)
                    .getData();
        } catch (VaultException e) {
            LOGGER.error("Error retrieving secrets '{}'.", vaultSecretPath, e);
            return new HashMap<>();
        }
    }

}
