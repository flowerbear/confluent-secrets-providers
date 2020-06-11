/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider.impl;

import io.confluent.kafka.secured.common.secretprovider.SecretsProvider;
import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfg;
import io.confluent.kafka.secured.common.secretprovider.SubscriptionHandler;
import org.apache.kafka.common.config.ConfigChangeCallback;
import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSecretsProvider<T extends SecretsProviderCfg> implements ConfigProvider, SecretsProvider<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractSecretsProvider.class);

    private SubscriptionHandler subscriptionHandler;
    private T configuration;

    @Override
    public ConfigData get(String storeName) {
        return get(storeName, null);
    }

    @Override
    public ConfigData get(String storeName, Set<String> keys) {
        final Map<String, String> cfg = new HashMap<>();

        if (keys != null) {
            // Load secrets
            keys.forEach(key -> cfg.put(key, getSecret(storeName, key)));
        }

        return new ConfigData(cfg);
    }

    @Override
    public void subscribe(String storeName, Set<String> keys, ConfigChangeCallback callback) {
        getSubscriptionHandler().subscribe(storeName, keys, callback);
    }

    @Override
    public void unsubscribe(String storeName, Set<String> keys, ConfigChangeCallback callback) {
        getSubscriptionHandler().unsubscribe(storeName, keys, callback);
    }

    @Override
    public void unsubscribeAll() {
        getSubscriptionHandler().unsubscribeAll();
    }

    @Override
    public void close() {
        if (subscriptionHandler != null) {
            subscriptionHandler.close();
            subscriptionHandler = null;
        }
    }

    @Override
    public void configure(Map<String, ?> properties) {
        this.configuration  = getConfig(properties);

        configure(configuration);
    }

    protected SubscriptionHandler getSubscriptionHandler() {
        if (subscriptionHandler == null) {
            synchronized (this) {
                if (subscriptionHandler == null) {
                    getSubscriptionHandler(configuration);
                    if (subscriptionHandler == null) {
                        subscriptionHandler = new NotImplementedSubscriptionHandler();
                    }
                }
            }
        }

        return subscriptionHandler;
    }

    private static class NotImplementedSubscriptionHandler implements SubscriptionHandler {

        @Override
        public void subscribe(String storeName, Set<String> keys, ConfigChangeCallback callback) {
            LOGGER.warn("Not supported.");
        }

        @Override
        public void unsubscribe(String storeName, Set<String> keys, ConfigChangeCallback callback) {
            LOGGER.warn("Not supported.");
        }

        @Override
        public void unsubscribeAll() {
            LOGGER.warn("Not supported.");
        }

        @Override
        public void close() {
        }
    }
}
