package com.github.fernthedev.gprefix.spigot;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.data.chat.TextMessage;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.DBUtil;
import com.github.fernthedev.gprefix.spigot.gui.PrefixListGui;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

@CommandPermission(Core.COMMAND_PERMISSION)
@CommandAlias("sprefix|spigotprefix")
public class SpigotCommand extends BaseCommand {

    @SneakyThrows
    @Description("Reload data")
    @CommandPermission(Core.COMMAND_PERMISSION + ".refresh")
    @Subcommand("refresh")
    @CommandCompletion("* *")
    public void onReload(FernCommandIssuer commandIssuer, ReloadType reloadType) {
        Universal.getScheduler().runAsync(() -> {
            try {
                switch (reloadType) {
                    case CONFIG:
                        commandIssuer.sendMessage(TextMessage.fromColor("&aReloading config"));
                        SpigotPlugin.getConfigDataConfig().syncLoad();
                        break;
                    case DATABASE:
                        DBUtil.StorageTypes storageTypes = Core.getPrefixPlugin().getCoreConfig().getConfigData().getStorageTypes();
                        String type;
                        switch (storageTypes) {
                            case PLUGIN:
                                type = "Bungeecord's database";
                                break;
                            case MYSQL:
                                type = "MySQL database";
                                break;
                            case JSON:
                                type = "JSON file";
                                break;
                            case YAML:
                                type = "YAML file";
                                break;
                            default:
                                type = storageTypes.name();
                        }

                        commandIssuer.sendMessage(TextMessage.fromColor("&aRequesting prefix list from " + type));
                        SpigotPlugin.getConfigDataConfig().getConfigData().getStorageTypes().getStorageHandler().load();
                        break;
                }
            } catch (Exception e) {
                throw new InvalidCommandArgument("Received error when reloading. Error: " + e.getLocalizedMessage());
            }
        });

    }

    public enum ReloadType {
        CONFIG,
        DATABASE
    }

    @Description("Open GUI Prefix Queue")
    @CommandPermission(Core.PREFIX_PERMISSION + ".gui")
    @CommandCompletion("@nothing")
    @Default
    public void openGui(Player fernCommandIssuer) {
        PrefixListGui.INVENTORY.open(fernCommandIssuer);
    }

    @CommandPermission(Core.COMMAND_PERMISSION)
    @HelpCommand
    public void onHelp(CommandIssuer commandIssuer, CommandHelp commandHelp) {
        commandHelp.showHelp(commandIssuer);
    }

}
