/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider;

import java.util.Map;

public interface SecretsProvider<T extends SecretsProviderCfg> {

    /**
     * Return an instance of {@link SubscriptionHandler} or null if the provider doesn't handler subscription capabilities
     * @param configuration of the secret provider
     * @return null or {@link SubscriptionHandler} instance
     */
    default SubscriptionHandler getSubscriptionHandler(T configuration) { return null; }

    /**
     * Return the value for a given key in the given store
     * @param storeName The store name
     * @param keyName The key name
     * @return The value or null if not found
     */
    String getSecret(final String storeName, final String keyName);

    /**
     * Return an instance of {@link SecretsProviderCfg}
     * @param properties used to construct the {@link SecretsProviderCfg}
     * @return <code>null</code> or an instance of {@link SecretsProviderCfg}.
     */
    T getConfig(Map<String, ?> properties);


    /**
     * Configure the provider
     * @param configuration returned by the <code>getConfig</code> method call.
     */
    void configure(T configuration);

}