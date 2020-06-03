package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.fernapi.universal.handlers.FernAPIPlugin;
import com.github.fernthedev.gprefix.core.db.impl.StorageHandler;

public interface PrefixPlugin extends FernAPIPlugin {

    default StorageHandler getStorageHandler() { return getCoreConfig().getConfigData().getStorageTypes().getStorageHandler(); }

    Config<? extends CommonConfigData> getCoreConfig();

    PrefixManager getPrefixManager();


}
