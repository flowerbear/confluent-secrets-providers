/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.vault;

import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfg;
import io.confluent.kafka.secured.common.secretprovider.SecretsProviderCfgBuilder;
import io.confluent.secrets.vault.utils.VaultClientCfg;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class VaultSecretProviderCfg extends SecretsProviderCfg {
    //TODO Add Documentation
    public static final String VAULT_URL_KEY = VaultClientCfg.VAULT_URL_KEY;
    public static final String VAULT_TOKEN_KEY =  VaultClientCfg.VAULT_TOKEN_KEY;
    public static final String KEYSTORE =  VaultClientCfg.KEYSTORE;
    public static final String KEYSTORE_PASSWORD =  VaultClientCfg.KEYSTORE_PASSWORD;
    public static final String TRUSTSTORE =  VaultClientCfg.TRUSTSTORE;
    public static final String RETRIES =  VaultClientCfg.RETRIES;
    public static final String INTERVAL =  VaultClientCfg.INTERVAL;
    public static final String VERSION =  VaultClientCfg.VERSION;

    private static final ConfigDef configDef;
    static {
        configDef = SecretsProviderCfgBuilder
                .newBuilder()
                .withPolling()
                .with(configDef -> configDef
                        .define(VAULT_URL_KEY, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "TODO")
                        .define(VAULT_TOKEN_KEY, ConfigDef.Type.PASSWORD, ConfigDef.Importance.HIGH, "TODO")
                        .define(KEYSTORE, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "TODO")
                        .define(KEYSTORE_PASSWORD, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "TODO")
                        .define(TRUSTSTORE, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "TODO")
                        .define(RETRIES, ConfigDef.Type.INT, 3, ConfigDef.Importance.HIGH, "TODO")
                        .define(INTERVAL, ConfigDef.Type.INT, 1000, ConfigDef.Importance.HIGH, "TODO")
                        .define(VERSION, ConfigDef.Type.INT, 1, ConfigDef.Importance.HIGH, "TODO"))
                .build();
    }

    public VaultSecretProviderCfg(final Map<String, ?> originals) {
        super(configDef, originals);
    }
}
