/*
 * Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.vault;

import io.confluent.secrets.cyberark.CyberarkSecretProvider;
import io.confluent.secrets.cyberark.CyberarkSecretProviderBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class TestCyberArk {

    @Test
    public void testGetPassword() {
        final CyberarkSecretProvider provider = CyberarkSecretProviderBuilder.newBuilder()
                .withAppID("ConfluentKafka")
                .withUrl("https://services-uscentral.skytap.com:18571")
                .build();

        provider.getSecret("Test", "ConfluenfftMongoDB");
    }
}
