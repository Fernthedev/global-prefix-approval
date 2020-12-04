package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.fernutils.thread.single.TaskInfo;
import com.github.fernthedev.gprefix.core.command.CoreCommands;
import com.github.fernthedev.gprefix.core.message.PrefixListPluginData;
import com.github.fernthedev.gprefix.core.message.PrefixRequestPluginData;
import com.github.fernthedev.gprefix.core.message.PrefixUpdateData;
import com.github.fernthedev.gprefix.spigot.db.PluginMessagingDB;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;

@NoArgsConstructor(access = AccessLevel.NONE)
public class Core {



    private static PrefixPlugin prefixPlugin;

    @Getter
    public static DateLogger dateLogger;

    public static final String NAMESPACE = "fprefix";

    public static final String COMMAND_PERMISSION = NAMESPACE + ".command";
    public static final String PREFIX_PERMISSION = COMMAND_PERMISSION + ".prefix";

    public static void init(PrefixPlugin prefixPlugin) {
        Core.prefixPlugin = prefixPlugin;

        File logFolder = new File(Universal.getMethods().getDataFolder(), "logs/");

        if (!logFolder.exists()) logFolder.mkdir();

        dateLogger = new DateLogger(logFolder);

        Config<? extends CommonConfigData> config = prefixPlugin.getCoreConfig();
        Universal.setDebug(config.getConfigData().isDebugMode());

        Universal.getMessageHandler().registerPacketParser(PrefixRequestPluginData.GSON_NAME, json -> new Gson().fromJson(json, PrefixRequestPluginData.class));
        Universal.getMessageHandler().registerPacketParser(PrefixListPluginData.GSON_NAME, json -> new Gson().fromJson(json, PrefixListPluginData.class));
        Universal.getMessageHandler().registerPacketParser(PrefixUpdateData.GSON_NAME, json -> new Gson().fromJson(json, PrefixUpdateData.class));

        Universal.getCommandHandler().enableUnstableAPI("help");

        Universal.getCommandHandler().registerCommand(new CoreCommands((PrefixPlugin) Universal.getPlugin()));
        prefixPlugin.getStorageHandler();
    }

    public static void disable() {
        Universal.getMethods().getAbstractLogger().info("Saving prefixes");

        TaskInfo<?> taskInfo = null;

        if (!(prefixPlugin.getStorageHandler() instanceof PluginMessagingDB))
            taskInfo = ThreadUtils.runAsync(() -> prefixPlugin.getStorageHandler().save(), ThreadUtils.ThreadExecutors.CACHED_THREADS.getExecutorService());

        TaskInfo<?> taskInfo1 = ThreadUtils.runAsync(() -> prefixPlugin.getCoreConfig().save(), ThreadUtils.ThreadExecutors.CACHED_THREADS.getExecutorService());

        if (taskInfo != null)
            taskInfo.awaitFinish(1);

        taskInfo1.awaitFinish(1);

        Universal.getMethods().getAbstractLogger().info("Save complete");
    }

    public static PrefixPlugin getPrefixPlugin() {
        return prefixPlugin;
    }
}
