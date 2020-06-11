/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider.impl;

import io.confluent.kafka.secured.common.secretprovider.SubscriptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class FileProvider extends AbstractSecretsProvider<FileProviderCfg> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProvider.class);

    @Override
    public SubscriptionHandler getSubscriptionHandler(FileProviderCfg configuration) {
        return null;
    }

    @Override
    public String getSecret(String storeName, String keyName) {

        try {
            final File file = new File(keyName);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            LOGGER.error("Error reading file '{}'", keyName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileProviderCfg getConfig(Map<String, ?> properties) {
        return new FileProviderCfg(properties);
    }

    @Override
    public void configure(FileProviderCfg cfg) {
    }
}
