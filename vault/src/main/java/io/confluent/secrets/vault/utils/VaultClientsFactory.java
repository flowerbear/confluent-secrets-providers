/*
 *  Copyright 2020 Confluent Inc.
 */

package io.confluent.secrets.vault.utils;

import com.bettercloud.vault.VaultException;

import java.util.HashMap;
import java.util.Map;

public class VaultClientsFactory {

    private final Map<String, VaultClient> clients = new HashMap<>();
    private static final VaultClientsFactory instance = new VaultClientsFactory();

    public static VaultClientsFactory getInstance() {
        return instance;
    }

    public VaultClient getSecrets(final VaultClientCfg clientCfg) throws VaultException {
        final String key = String.format("%s.%s", clientCfg.getUrl(), clientCfg.getToken());

        VaultClient secrets = clients.get(key);
        if (secrets == null) {
            synchronized (this) {
                secrets = clients.get(key);
                if (secrets == null) {
                    secrets = new VaultClient(clientCfg);
                    clients.put(key, secrets);
                }
            }
        }

        return secrets;
    }

}
