/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider;

import org.apache.kafka.common.config.ConfigDef;

public class SecretsProviderCfgBuilder {
    public interface CustomDefine {
        void define(final ConfigDef configDef);
    }

    private final ConfigDef configDef = new ConfigDef();

    public ConfigDef getConfigDef() {
        return configDef;
    }

    protected SecretsProviderCfgBuilder() {
        super();
    }

    public static SecretsProviderCfgBuilder newBuilder() {
        return new SecretsProviderCfgBuilder();
    }

    public SecretsProviderCfgBuilder withPolling() {
        configDef
                .define(SecretsProviderCfg.POLL_INTERVAL, ConfigDef.Type.INT, 60, ConfigDef.Importance.HIGH, "TODO");

        return this;
    }

    public SecretsProviderCfgBuilder with(final CustomDefine customDefine) {
        customDefine.define(getConfigDef());
        return this;
    }

    public ConfigDef build() {
        return configDef;
    }

}
