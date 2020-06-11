/*
 * Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.cyberark;

import java.util.HashMap;
import java.util.Map;

/**
 * CyberArk provider builder.
 * This class will create all properties required to configure
 * Cyberark provider via configure method.
 */
public class CyberarkSecretProviderBuilder {

    private String url;
    private String appID;
    private String object;
    private String safe;

    public CyberarkSecretProviderBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public CyberarkSecretProviderBuilder withAppID(String appID) {
        this.appID = appID;
        return this;
    }

    public CyberarkSecretProviderBuilder withObject(String object) {
        this.object = object;
        return this;
    }

    public CyberarkSecretProviderBuilder withSafe(String safe) {
        this.safe = safe;
        return this;
    }

    private CyberarkSecretProviderBuilder() {}

    public static CyberarkSecretProviderBuilder newBuilder() {
        return new CyberarkSecretProviderBuilder();
    }

    /**
     * Build the provider
     * @return Instance of the provider
     */
    public CyberarkSecretProvider build() {
        final Map<String, Object> properties = new HashMap<String, Object>() {{
            put(CyberarkSecretProviderCfg.APPID, appID);
            put(CyberarkSecretProviderCfg.HOST, url);
        }};

        final CyberarkSecretProvider provider = new CyberarkSecretProvider();
        provider.configure(properties);

        return provider;
    }
}
