/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.aws;

import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfg;
import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfgBuilder;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class AWSSecretProviderCfg extends SecretsProviderCfg {
    // TODO: Documentation
    public static String REGION = "region";

    private static final ConfigDef configDef = SecretsProviderCfgBuilder
                .newBuilder()
                .withPolling()
                .with(configDef -> configDef
                        .define(REGION, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "TODO"))
                .build();

    protected AWSSecretProviderCfg(final Map<?, ?> originals) {
        super(configDef, originals);
    }

}