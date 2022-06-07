package com.github.fernthedev.gprefix.spigot;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernapi.server.spigot.FernSpigotAPI;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.core.CommonConfigData;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixPlugin;
import com.github.fernthedev.gprefix.spigot.db.PluginMessagingDB;
import com.github.fernthedev.gprefix.spigot.hooks.LuckPermsPrefixHandler;
import com.github.fernthedev.gprefix.spigot.hooks.NametageditPrefixHandler;
import com.github.fernthedev.gprefix.spigot.hooks.TABPrefixHandler;
import com.github.fernthedev.gprefix.spigot.hooks.VaultPrefixHandler;
import com.github.fernthedev.gprefix.spigot.locale.GuiLocale;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;

public class SpigotPlugin extends FernSpigotAPI implements PrefixPlugin {

    private static SpigotPlugin spigotPlugin;

    public static SpigotPlugin getInstance() {
        return spigotPlugin;
    }

    @Getter
    private static GsonConfig<SpigotConfigData> configDataConfig;

    private SpigotPrefixManager prefixManager;

    private static PluginMessagingDB pluginMessagingDB;
    public static PluginMessagingDB getPluginManager() {
        if (pluginMessagingDB != null) return pluginMessagingDB;

        pluginMessagingDB = new PluginMessagingDB();
        pluginMessagingDB.init();
        return pluginMessagingDB;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        spigotPlugin = this;

        prefixManager = new SpigotPrefixManager();


        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        try {
            configDataConfig = new GsonConfig<>(new SpigotConfigData(), new File(getDataFolder(), "config.json"));
            configDataConfig.load();
            configDataConfig.setGson(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create());
            configDataConfig.save();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Core.init(this);




//            FernCommands.getInstance().addMessageListener(new SpigotPrefixManager());




        Universal.getCommandHandler().registerCommand(new SpigotCommand());

        if (getServer().getPluginManager().isPluginEnabled("NametagEdit"))
            getServer().getPluginManager().registerEvents(new NametageditPrefixHandler(), this);


        if (getServer().getPluginManager().isPluginEnabled("VaultAPI"))
            getServer().getPluginManager().registerEvents(new VaultPrefixHandler(), this);

        if (getServer().getPluginManager().isPluginEnabled("TAB"))
            getServer().getPluginManager().registerEvents(new TABPrefixHandler(), this);

        else if (getServer().getPluginManager().isPluginEnabled("LuckPerms"))
            getServer().getPluginManager().registerEvents(new LuckPermsPrefixHandler(), this);


    }

    public static SpigotConfigData getConfigData() {
        return configDataConfig.getConfigData();
    }

    public static GuiLocale guiLocale() {
        return configDataConfig.getConfigData().getGuiLocale();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Core.disable();
    }



    @Override
    public Config<? extends CommonConfigData> getCoreConfig() {
        return configDataConfig;
    }

    @Override
    public SpigotPrefixManager getPrefixManager() {
        return prefixManager;
    }


}
