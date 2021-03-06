package com.github.fernthedev.gprefix.bungee;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernapi.server.bungee.FernBungeeAPI;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.bungee.hooks.LuckPermsPrefixHandler;
import com.github.fernthedev.gprefix.core.CommonConfigData;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixPlugin;
import com.github.fernthedev.gprefix.proxy.ProxyPrefixCommand;
import com.github.fernthedev.gprefix.proxy.ProxyConfigData;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.File;

public class BungeePlugin extends FernBungeeAPI implements PrefixPlugin {

    private static BungeePlugin bungeePlugin;

    public static BungeePlugin getInstance() {
        return bungeePlugin;
    }

    @Getter
    private BungeePrefixManager prefixManager;

    @Getter
    private static GsonConfig<ProxyConfigData> dataConfig;

    @Override
    public void onEnable() {
        super.onEnable();
        bungeePlugin = this;
        prefixManager = new BungeePrefixManager();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

//            FernCommands.getInstance().addMessageListener(new SpigotPrefixManager());

        try {
            dataConfig = new GsonConfig<>(new ProxyConfigData(), new File(getDataFolder(), "config.json"));
            dataConfig.load();
            dataConfig.setGson(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create());
            dataConfig.save();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
            onDisable();
            return;
        }

        Core.init(this);


        Universal.getCommandHandler().registerCommand(new ProxyPrefixCommand());

        BungeePrefixManager networkManager = new BungeePrefixManager();
        Universal.getMessageHandler().registerMessageHandler(networkManager);
        getProxy().getPluginManager().registerListener(this, networkManager);

        getLogger().info("Registered fern prefix bungee channels.");

        if (getProxy().getPluginManager().getPlugin("LuckPerms") != null)
            getProxy().getPluginManager().registerListener(this, new LuckPermsPrefixHandler());
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling global prefixes and saving");
        super.onDisable();

        Core.disable();
    }

    @Override
    public Config<? extends CommonConfigData> getCoreConfig() {
        return dataConfig;
    }


}
