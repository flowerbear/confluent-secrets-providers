/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.kafka.secured.common.secretprovider.utils;

import org.apache.kafka.common.config.ConfigData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SecretsMonitorTests {

    @Test
    public void TestSecretsMonitor() {

        final Map<String, String> values = new HashMap<String, String>() {{
            put("Key1", "value1");
            put("Key2", "value1"); }};

        SecretsMonitor secretsMonitor = new SecretsMonitor("aStore", () -> values);

        AtomicReference<String> changedStore = new AtomicReference<>();
        AtomicReference<ConfigData>  changedValue = new AtomicReference<>();
        secretsMonitor.addKeys(new HashSet<>(Arrays.asList("Key1", "Key2")), (store, value) -> {
            changedStore.set(store);
            changedValue.set(value);
        } );

        values.put("Key2", "AnotherValue");

        secretsMonitor.computeChanges();

        Assertions.assertEquals("aStore", changedStore.get());
        Assertions.assertNotNull(changedValue.get());
        Assertions.assertNotNull(changedValue.get().data());
        Assertions.assertEquals(1, changedValue.get().data().size());
        Assertions.assertTrue(changedValue.get().data().containsKey("Key2"));
        Assertions.assertEquals("AnotherValue", changedValue.get().data().get("Key2"));

        values.put("Key1", "ChangedValue");
        values.put("Key2", "NewValue");

        secretsMonitor.computeChanges();

        Assertions.assertEquals("aStore", changedStore.get());
        Assertions.assertNotNull(changedValue.get());
        Assertions.assertNotNull(changedValue.get().data());
        Assertions.assertEquals(2, changedValue.get().data().size());
        Assertions.assertTrue(changedValue.get().data().containsKey("Key1"));
        Assertions.assertTrue(changedValue.get().data().containsKey("Key2"));
        Assertions.assertEquals("ChangedValue", changedValue.get().data().get("Key1"));
        Assertions.assertEquals("NewValue", changedValue.get().data().get("Key2"));

        values.remove("Key1");

        secretsMonitor.computeChanges();

        Assertions.assertEquals("aStore", changedStore.get());
        Assertions.assertNotNull(changedValue.get());
        Assertions.assertNotNull(changedValue.get().data());
        Assertions.assertEquals(1, changedValue.get().data().size());
        Assertions.assertTrue(changedValue.get().data().containsKey("Key1"));
        Assertions.assertNull(changedValue.get().data().get("Key1"));

        values.put("Key1", "NewKey1");

        secretsMonitor.computeChanges();

        Assertions.assertEquals("aStore", changedStore.get());
        Assertions.assertNotNull(changedValue.get());
        Assertions.assertNotNull(changedValue.get().data());
        Assertions.assertEquals(1, changedValue.get().data().size());
        Assertions.assertTrue(changedValue.get().data().containsKey("Key1"));
        Assertions.assertEquals("NewKey1",changedValue.get().data().get("Key1"));
    }

}
