/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider.impl;

import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfg;

import java.util.Map;

public class FileProviderCfg extends SecretsProviderCfg {

    public FileProviderCfg(final Map<String, ?> properties) {
        super(properties);
    }

}