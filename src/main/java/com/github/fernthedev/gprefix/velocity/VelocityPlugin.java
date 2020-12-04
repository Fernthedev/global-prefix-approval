package com.github.fernthedev.gprefix.velocity;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernapi.server.velocity.FernVelocityAPI;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.core.CommonConfigData;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixPlugin;
import com.github.fernthedev.gprefix.proxy.ProxyPrefixCommand;
import com.github.fernthedev.gprefix.proxy.ProxyConfigData;
import com.google.gson.GsonBuilder;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.File;

@Plugin(id = "fern_global_prefixes", name = "FernGlobalPrefixes", version = "0.3.0-BETA", authors = "Fernthedev", dependencies = @Dependency(id = "luckperms", optional = true))
public class VelocityPlugin extends FernVelocityAPI implements PrefixPlugin {

    private static VelocityPlugin velocityPlugin;
    @Getter
    private static GsonConfig<ProxyConfigData> dataConfig;

    @Getter
    private final VelocityPrefixManager prefixManager;

    public VelocityPlugin(ProxyServer server, Logger logger) {
        super(server, logger);
        velocityPlugin = this;

        prefixManager = new VelocityPrefixManager();
    }

    public static VelocityPlugin getInstance() {
        return velocityPlugin;
    }

    @Override
    public void onProxyInitialization(ProxyInitializeEvent event) {
        super.onProxyInitialization(event);


        File dataFolder = dataDirectory.toFile();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

//            FernCommands.getInstance().addMessageListener(new SpigotPrefixManager());

        try {
            dataConfig = new GsonConfig<>(new ProxyConfigData(), new File(dataFolder, "config.json"));
            dataConfig.load();
            dataConfig.setGson(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create());
            dataConfig.save();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
            onProxyStop(new ProxyShutdownEvent());
            return;
        }

        Core.init(this);


        Universal.getCommandHandler().registerCommand(new ProxyPrefixCommand());

        VelocityPrefixManager networkManager = new VelocityPrefixManager();
        Universal.getMessageHandler().registerMessageHandler(networkManager);
        server.getEventManager().register(this, new VelocityPrefixManager());

        getLogger().info("Registered fern prefix bungee channels.");
    }

    @Override
    public void onProxyStop(ProxyShutdownEvent event) {
        getLogger().info("Disabling global prefixes and saving");

        super.onProxyStop(event);

        Core.disable();
    }


    @Override
    public Config<? extends CommonConfigData> getCoreConfig() {
        return dataConfig;
    }


}
