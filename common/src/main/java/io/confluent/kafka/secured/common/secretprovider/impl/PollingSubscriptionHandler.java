/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider.impl;

import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfg;
import io.confluent.kafka.secured.common.secretprovider.SubscriptionHandler;
import io.confluent.kafka.secured.common.secretprovider.utils.PollingHandler;
import io.confluent.kafka.secured.common.secretprovider.utils.SecretsMonitor;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigChangeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PollingSubscriptionHandler implements SubscriptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PollingSubscriptionHandler.class);

    public interface Supplier {
        Map<String, String> getSecrets(final String storeName);
    }

    private final Supplier valueSupplier;
    private final PollingHandler pollingHandler;
    private final Map<String, SecretsMonitor> monitors = new ConcurrentHashMap<>();

    public PollingSubscriptionHandler(final AbstractConfig config, final Supplier valueSupplier) {
        Objects.requireNonNull(valueSupplier);
        Objects.requireNonNull(config);

        this.valueSupplier = valueSupplier;

        final int poll = config.getInt(SecretsProviderCfg.POLL_INTERVAL);
        if (poll == 0) {
            LOGGER.warn("Poll interval defined to 0. Polling will be disabled.");
            pollingHandler = null;
        } else {
            pollingHandler = new PollingHandler(poll, this::onPoll);
        }
    }

    @Override
    public synchronized void subscribe(String storeName, Set<String> keys, ConfigChangeCallback callback) {
        if (pollingHandler == null) {
            return;
        }

        final SecretsMonitor monitor = monitors.computeIfAbsent(storeName, (name) -> new SecretsMonitor(name,() -> valueSupplier.getSecrets(name)));
        monitor.addKeys(keys, callback);
    }

    @Override
    public void unsubscribe(String storeName, Set<String> keys, ConfigChangeCallback callback) {
        if (pollingHandler == null) {
            return;
        }

        final SecretsMonitor monitor = monitors.get(storeName);
        if (monitor != null) {
            monitor.removeKeys(keys, callback);
        }
    }

    @Override
    public synchronized void unsubscribeAll() {
        if (pollingHandler == null) {
            return;
        }

        monitors.forEach((k, monitor) -> monitor.clearAllKeys());
        monitors.clear();
    }

    @Override
    public void close() {
        if (pollingHandler == null) {
            return;
        }

        unsubscribeAll();
        pollingHandler.stop();
    }

    private synchronized void onPoll() {
        monitors.forEach((k, monitor) -> monitor.computeChanges());
    }
}
