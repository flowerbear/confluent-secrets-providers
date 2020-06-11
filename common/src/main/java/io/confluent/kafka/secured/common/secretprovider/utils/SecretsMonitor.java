/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.config.ConfigChangeCallback;
import org.apache.kafka.common.config.ConfigData;

import java.util.*;
import java.util.function.Supplier;

public class SecretsMonitor {

    private final Map<ConfigChangeCallback, Map<String, String>> callbacks = new HashMap<>();
    private final Supplier<Map<String, String>> supplier;
    private final String storeName;

    public SecretsMonitor(final String storeName, final Supplier<Map<String, String>> supplier) {
        this.supplier = supplier;
        this.storeName = storeName;
    }

    /**
     * Add keys to monitor for a specific callback
     * @param keys to monitor
     * @param callback callback to call when a given key is modified
     */
    public synchronized void addKeys(final Set<String> keys, ConfigChangeCallback callback) {
        final Map<String, String> currentValues = supplier.get();

        final Map<String, String> values = callbacks.computeIfAbsent(callback, (k) -> new HashMap<>());

        keys.forEach(key -> {
            if (currentValues.containsKey(key)) {
                values.put(key, currentValues.get(key));
            }
        });
    }

    public synchronized void removeKeys(final Set<String> keys, ConfigChangeCallback callback) {
        final Map<String, String> values = callbacks.get(callback);
        if (values == null) {
            return;
        }

        // Remove all key/value to monitor for keys
        keys.forEach(values::remove);

        // If empty just remove the callback
        if (values.isEmpty()) {
            callbacks.remove(callback);
        }
    }

    public synchronized void clearAllKeys() {
        callbacks.clear();
    }

    public synchronized void computeChanges() {
        final Map<String, String> newValues = supplier.get();

        callbacks.forEach((callback, values) -> {
            final Map<String, String> changedValues = new HashMap<>();

            values.forEach((key, value) -> {
                final String newValue = newValues.get(key);
                if (!StringUtils.equals(value, newValue)) {
                    changedValues.put(key, newValue);
                    values.put(key, newValue);
                }
            });

            if (!changedValues.isEmpty()) {
                callback.onChange(storeName, new ConfigData(changedValues));
            }
        });

    }

}
