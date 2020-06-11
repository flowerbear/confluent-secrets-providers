/*
 * Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.cyberark;

import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfg;
import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfgBuilder;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class CyberarkSecretProviderCfg extends SecretsProviderCfg {
    public static final String APPID = "cyberark.app.id";
    public static final String APPID_DOC = "Cyberark application ID.";

    public static final String HOST = "cyberark.url";
    public static final String HOST_DOC = "Cyber url";

    private static final ConfigDef configDef;
    static {
        configDef = SecretsProviderCfgBuilder
                .newBuilder()
                .with(configDef -> configDef
                        .define(APPID, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, APPID_DOC)
                        .define(HOST, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, HOST_DOC))
                .build();
    }

    public CyberarkSecretProviderCfg(final Map<String, ?> originals) {
        super(configDef, originals);
    }
}
