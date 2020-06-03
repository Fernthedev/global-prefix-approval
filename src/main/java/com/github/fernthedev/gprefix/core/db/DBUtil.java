package com.github.fernthedev.gprefix.core.db;

import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.config.snakeyaml.SnakeYamlEngineConfig;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.handlers.ServerType;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.config.ConfigStorageHandler;
import com.github.fernthedev.gprefix.core.db.impl.StorageHandler;
import com.github.fernthedev.gprefix.core.db.mysql.MysqlStorageHandler;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.File;

public class DBUtil {


    private static ConfigStorageHandler gsonConfig;
    private static ConfigStorageHandler yamlConfig;
    private static MysqlStorageHandler mysqlStorage;

    @SneakyThrows
    private static ConfigStorageHandler sneakyThrowGsonConfig() {
        if (gsonConfig != null) return gsonConfig;

        GsonConfig<ConfigStorageHandler.StorageData> storageDataGsonConfig = new GsonConfig<>(new ConfigStorageHandler.StorageData(Core.getPrefixPlugin().getPrefixManager().getPrefixes()), prefixFile(".json"));
        storageDataGsonConfig.setGson(new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().create());
        gsonConfig = new ConfigStorageHandler(storageDataGsonConfig);
        gsonConfig.init();
        return gsonConfig;
    }

    @SneakyThrows
    private static ConfigStorageHandler sneakyThrowYamlConfig() {
        if (yamlConfig != null) return yamlConfig;

        yamlConfig = new ConfigStorageHandler(new SnakeYamlEngineConfig<>(new ConfigStorageHandler.StorageData(Core.getPrefixPlugin().getPrefixManager().getPrefixes()), prefixFile(".yaml")));
        yamlConfig.init();
        return yamlConfig;
    }

    private static MysqlStorageHandler getMysql() {
        if (mysqlStorage != null) return mysqlStorage;

        mysqlStorage = new MysqlStorageHandler();
        mysqlStorage.init();
        return mysqlStorage;
    }

    public static StorageHandler getFromEnum(StorageTypes storageTypes) {
        switch (storageTypes) {
            case JSON:
                return sneakyThrowGsonConfig();
            case YAML:
                return sneakyThrowYamlConfig();
            case MYSQL:
                return getMysql();
            case PLUGIN:
                if (Universal.getMethods().getServerType() == ServerType.BUKKIT) {
                    return SpigotPlugin.getPluginManager();
                } else {
                    throw new IllegalStateException("The PLUGIN messaging Storage Handler is only Bukkit compatible.");
                }
        }

        return null;
    }

    private static File prefixFile(String extension) {return new File(Universal.getMethods().getDataFolder(), "prefixes." + extension); }




    public enum StorageTypes {
        PLUGIN,
        MYSQL,
        JSON,
        YAML;

        public StorageHandler getStorageHandler() {
            return getFromEnum(this);
        }
    }

}
