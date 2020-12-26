package com.github.fernthedev.gprefix.core.db.config;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.db.impl.StorageHandler;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ConfigStorageHandler implements StorageHandler {

    @NonNull
    private final Config<StorageData> storageDataConfig;

    @Override
    public void init() {
        Universal.getScheduler().runAsync(() -> {
            load();
            save();
        });
    }

    @SneakyThrows
    @Override
    public CompletableFuture<?> save() {
        return Universal.getScheduler().runAsync(() -> {
            storageDataConfig.getConfigData().prefixInfoDataMap = Core.getPrefixPlugin().getPrefixManager().getPrefixes();
            try {
                storageDataConfig.syncSave();
            } catch (ConfigLoadException e) {
                e.printStackTrace();
            }
        }).getTaskFuture();
    }

    @SneakyThrows
    @Override
    public CompletableFuture<?> load() {
        return Universal.getScheduler().runAsync(() -> {
            try {
                storageDataConfig.syncLoad();
            } catch (ConfigLoadException e) {
                e.printStackTrace();
                return;
            }
            Core.getPrefixPlugin().getPrefixManager().getPrefixes().clear();
            Core.getPrefixPlugin().getPrefixManager().getPrefixes().putAll(storageDataConfig.getConfigData().prefixInfoDataMap);
        }).getTaskFuture();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StorageData {
        private Map<UUID, PrefixInfoData> prefixInfoDataMap = new HashMap<>();
    }
}
