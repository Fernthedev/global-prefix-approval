package com.github.fernthedev.gprefix.core.db.config;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.db.impl.StorageHandler;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public void save() {
        storageDataConfig.getConfigData().prefixInfoDataMap = Core.getPrefixPlugin().getPrefixManager().getPrefixes();
        storageDataConfig.syncSave();
    }

    @SneakyThrows
    @Override
    public void load() {
        storageDataConfig.syncLoad();
        Core.getPrefixPlugin().getPrefixManager().getPrefixes().clear();
        Core.getPrefixPlugin().getPrefixManager().getPrefixes().putAll(storageDataConfig.getConfigData().prefixInfoDataMap);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StorageData {
        private Map<UUID, PrefixInfoData> prefixInfoDataMap = new HashMap<>();
    }
}
