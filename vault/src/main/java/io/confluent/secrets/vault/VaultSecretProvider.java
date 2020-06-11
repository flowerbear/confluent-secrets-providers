/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.vault;

import com.bettercloud.vault.VaultException;
import io.confluent.kafka.secured.common.secretprovider.SubscriptionHandler;
import io.confluent.kafka.secured.common.secretprovider.impl.AbstractSecretsProvider;
import io.confluent.kafka.secured.common.secretprovider.impl.PollingSubscriptionHandler;
import io.confluent.secrets.vault.utils.VaultClient;
import io.confluent.secrets.vault.utils.VaultClientCfg;
import io.confluent.secrets.vault.utils.VaultClientsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VaultSecretProvider extends AbstractSecretsProvider<VaultSecretProviderCfg> implements PollingSubscriptionHandler.Supplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaultSecretProvider.class);

    private VaultClient vaultClient;
    private Map<String, Map<String, String>> secrets = new ConcurrentHashMap<>();

    @Override
    public SubscriptionHandler getSubscriptionHandler(VaultSecretProviderCfg cfg) {
        return new PollingSubscriptionHandler(cfg,this);
    }

    @Override
    public String getSecret(String storeName, String keyName) {
        final Map<String, String> values = secrets.computeIfAbsent(storeName, this::loadAllSecrets);

        return values.get(keyName);
    }

    @Override
    public VaultSecretProviderCfg getConfig(Map<String, ?> properties) {
        return new VaultSecretProviderCfg(properties);
    }

    @Override
    public void configure(VaultSecretProviderCfg cfg) {
        final VaultClientCfg clientCfg = VaultClientCfg.getClientCfg(cfg);
        try {
            vaultClient = VaultClientsFactory.getInstance().getSecrets(clientCfg);
        } catch (VaultException e) {
            LOGGER.error("Error getting Vault client.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getSecrets(String storeName) {
        final Map<String, String> values = vaultClient.getSecrets(storeName, true);
        secrets.put(storeName, values);

        return values;
    }

    private Map<String, String> loadAllSecrets(String storeName) {
        return vaultClient.getSecrets(storeName, false);
    }
}
