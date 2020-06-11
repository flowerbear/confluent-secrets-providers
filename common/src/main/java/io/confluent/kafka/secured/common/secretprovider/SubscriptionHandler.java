/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider;

import org.apache.kafka.common.config.ConfigChangeCallback;

import java.util.Set;

/**
 * A subscription handler to support subscriptions to configuration changes.
 */
public interface SubscriptionHandler {

    /**
     * Subscribes to changes for the given keys at the given path (optional operation).
     * @param storeName the store where the data resides
     * @param keys the keys whose values will be retrieved
     * @param callback the callback to invoke upon change
     */
    void subscribe(String storeName, Set<String> keys, ConfigChangeCallback callback);

    /**
     * Unsubscribes to changes for the given keys at the given path (optional operation).
     * @param storeName the store where the data resides
     * @param keys the keys whose values will be retrieved
     * @param callback the callback to invoke upon change
     */
    void unsubscribe(String storeName, Set<String> keys, ConfigChangeCallback callback);

    /**
     * Clears all subscribers (optional operation).
     */
    void unsubscribeAll();

    /**
     * Close the subscription handler.
     */
    void close();
}
