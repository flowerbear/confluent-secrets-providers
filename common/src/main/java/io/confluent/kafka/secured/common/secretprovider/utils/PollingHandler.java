/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PollingHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(PollingHandler.class);

    public interface Handler {
        void doPolling();
    }

    private final ScheduledFuture<?> scheduledFuture;
    private final Handler handler;

    public PollingHandler(final long handlerPeriod, final Handler handler) {
        this.handler = handler;

        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        this.scheduledFuture = executor.scheduleAtFixedRate(this::timeoutHandler, handlerPeriod, handlerPeriod, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduledFuture.cancel(true);
    }

    private void timeoutHandler() {
        try {
            handler.doPolling();
        } catch (RuntimeException e) {
            LOGGER.error("Error during handler invocation.", e);
        }
    }

}