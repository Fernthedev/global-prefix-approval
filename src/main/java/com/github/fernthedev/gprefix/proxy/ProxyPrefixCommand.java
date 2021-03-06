package com.github.fernthedev.gprefix.proxy;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.data.chat.TextMessage;
import com.github.fernthedev.gprefix.core.Core;
import lombok.SneakyThrows;

@CommandAlias("bungeerefix|bprefix")
public class ProxyPrefixCommand extends BaseCommand {

    public enum ReloadType {
        CONFIG,
        DATABASE
    }

    @SneakyThrows
    @Description("Reload data")
    @CommandPermission(Core.COMMAND_PERMISSION + ".reload")
    @Subcommand("reload")
    @CommandCompletion("* *")
    public void onReload(FernCommandIssuer fernCommandIssuer, ReloadType reloadType) {
        switch (reloadType) {
            case CONFIG:
                fernCommandIssuer.sendMessage(TextMessage.fromColor("&aReloading config"));
                Core.getPrefixPlugin().getCoreConfig().syncLoad();
                Universal.setDebug(Core.getPrefixPlugin().getCoreConfig().getConfigData().isDebugMode());
                break;
            case DATABASE:
                fernCommandIssuer.sendMessage(TextMessage.fromColor("&aReloading database"));
                Universal.getScheduler().runAsync(() -> {
                    Core.getPrefixPlugin().getStorageHandler().load();
                    fernCommandIssuer.sendMessage(TextMessage.fromColor("&aFinished database"));
                });
                break;
        }
    }

    @CommandPermission(Core.COMMAND_PERMISSION)
    @HelpCommand
    public void onHelp(CommandIssuer commandIssuer, CommandHelp commandHelp) {
        commandHelp.showHelp(commandIssuer);
    }


}
