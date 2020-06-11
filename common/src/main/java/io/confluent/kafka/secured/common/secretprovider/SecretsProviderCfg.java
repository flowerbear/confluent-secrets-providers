/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class SecretsProviderCfg extends AbstractConfig {
    // TODO: Documentation
    public static final String POLL_INTERVAL = "poll";

    protected static ConfigDef getConfigDef() {
        return SecretsProviderCfgBuilder
                .newBuilder()
                .build();
    }

    protected SecretsProviderCfg(final Map<?, ?> originals) {
        super(getConfigDef(), originals, true);
    }

    protected SecretsProviderCfg(final ConfigDef configDef, final Map<?, ?> originals) {
        super(configDef, originals, true);
    }
}
