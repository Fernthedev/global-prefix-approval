package com.github.fernthedev.gprefix.core.command;

import co.aikar.commands.*;
import co.aikar.commands.annotation.*;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.fernapi.universal.data.chat.*;
import com.github.fernthedev.fernapi.universal.util.UUIDFetcher;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixPlugin;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias(CoreCommands.COMMAND_ALIAS + "|fprefix|gprefix")
@RequiredArgsConstructor
public class CoreCommands extends BaseCommand {

    public final static String COMMAND_ALIAS = "prefix";
    public final static String APPROVE_SUBCOMAMND = "approve";
    public final static String DENY_SUBCOMMAND = "deny";

    private static final int PAGE_SIZE = 10;

    @NonNull
    private final PrefixPlugin prefixPlugin;

    @CommandPermission(Core.COMMAND_PERMISSION + ".request")
    @Description("Change prefix using MySQL")
    @CommandCompletion("@nothing")
    @Subcommand("request")
    public void onPrefix(IFPlayer<?> sender, String[] newPrefix/*, @CommandPermission(Core.PREFIX_PERMISSION + ".others") @Flags("other,defaultself") IFPlayer<?> player*/) {

//        Connection connection = DatabaseHandler.getConnection();

        if (!sender.hasPermission(Core.PREFIX_PERMISSION + ".allowSpaces") && newPrefix.length > 0) {
            sender.sendMessage(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getNoSpacingAllowed());
            return;
        }

        StringBuilder newPrefixBuilder = new StringBuilder();

        for (String s : newPrefix) newPrefixBuilder.append(s).append(" ");

        String newPrefixStr = newPrefixBuilder.substring(0, newPrefixBuilder.length() - 1);

//        if (!sender.isPlayer() && player == null) {
//            sender.sendError(MessageKeys.PLEASE_SPECIFY_ONE_OF, "{valid}", "player");
//            return;
//        }

        if (!sender.hasPermission(Core.PREFIX_PERMISSION + ".color") && !ChatColor.stripColor(newPrefixStr).equals(newPrefixStr)) {

            sender.sendMessage(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getNoColorsAllowed());
            return;
        }

        String checkLength = newPrefixStr;

        if (!prefixPlugin.getCoreConfig().getConfigData().isIncludeColorCodesInLength()) checkLength = ChatColor.stripColor(checkLength);

        if (!sender.hasPermission(Core.PREFIX_PERMISSION + ".exceedLength")) {

            int prefixLength = prefixPlugin.getCoreConfig().getConfigData().getPrefixLength();

            if (checkLength.length() > prefixLength) {
                sender.sendMessage(
                        TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixLengthExceeded()
                                .replace("${prefix}", newPrefixStr)
                                .replace("${length}", prefixLength + "")
                        ));
                return;
            }
        }

        //                if(connection != null) {

        applyPrefix(sender, newPrefixStr);
        sender.sendMessage(TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getReviewInProcess()));
    }

    @Subcommand("list")
    @CommandPermission(Core.COMMAND_PERMISSION + ".list")
    public void listQueue(FernCommandIssuer fernCommandIssuer, @Optional Integer page) {

        // 55 items
        // 5 = 50th
        // 6 = 60th


        if (page == null) page = 1;

        Map<UUID, String> prefixMap = prefixPlugin.getPrefixManager().getPrefixQueueMap();

        List<UUID> uuidList = new ArrayList<>(prefixMap.keySet());


        if (uuidList.isEmpty()) {
            fernCommandIssuer.sendMessage(TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getQueueIsEmpty()));
            return;
        }

        int maxPage = uuidList.size() / 10;

        if (uuidList.size() % PAGE_SIZE != 0) maxPage++;

        if (page > PAGE_SIZE) {
            throw new InvalidCommandArgument("Went over maximum page which is: " + maxPage);
        }



        List<UUID> pageUUIDs = Lists.partition(uuidList, PAGE_SIZE).get(page - 1);

        pageUUIDs.forEach(uuid -> fernCommandIssuer.sendMessage(getPlayerMessage(uuid, prefixMap.get(uuid))));
    }

    @Subcommand("approve")
    @CommandPermission(Core.COMMAND_PERMISSION + ".approve")
    @CommandCompletion("@players true|false")
    public void approve(FernCommandIssuer fernCommandIssuer, @Flags("other,offline") IFPlayer<?> player, @Optional @Default("false") boolean silent) {
        if (Core.getPrefixPlugin().getPrefixManager().getPrefixQueueMap().containsKey(player.getUniqueId())) {
            PrefixInfoData prefixData = Core.getPrefixPlugin().getPrefixManager().getPrefixes().get(player.getUniqueId());


            prefixPlugin.getPrefixManager().updatePrefixStatus(fernCommandIssuer, player, CommonNetwork.PrefixUpdateMode.APPROVED, silent);
            fernCommandIssuer.sendMessage(TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixApproved().replace("${player}", player.getName()).replace("${prefix}", prefixData.getPrefix())));

        } else
            fernCommandIssuer.sendMessage(TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getQueueDoesNotContain().replace("${player}", player.getName())));
    }

    @Subcommand("deny")
    @CommandCompletion("@players true|false")
    public void deny(FernCommandIssuer fernCommandIssuer, @Flags("other,offline") IFPlayer<?> player,  @Optional @Default("false") boolean silent) {
        if (Core.getPrefixPlugin().getPrefixManager().getPrefixQueueMap().containsKey(player.getUniqueId())) {
            PrefixInfoData prefixData = Core.getPrefixPlugin().getPrefixManager().getPrefixes().get(player.getUniqueId());


            prefixPlugin.getPrefixManager().updatePrefixStatus(fernCommandIssuer, player, CommonNetwork.PrefixUpdateMode.DENIED, silent);
            fernCommandIssuer.sendMessage(TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixDenied().replace("${player}", player.getName()).replace("${prefix}", prefixData.getPrefix())));

        } else
            fernCommandIssuer.sendMessage(TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getQueueDoesNotContain().replace("${player}", player.getName())));
    }

    @CommandPermission(Core.COMMAND_PERMISSION)
    @HelpCommand
    @Default
    public void onHelp(CommandIssuer commandIssuer, CommandHelp commandHelp) {
        commandHelp.showHelp(commandIssuer);
    }

    private BaseMessage getPlayerMessage(UUID uuid, String prefix) {
        String name = UUIDFetcher.getName(uuid);

        TextMessage approveButton = TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixApproveButton().replace("${prefix}", prefix)
                .replace("${player}", name));
        approveButton.setClickData(new ClickData(ClickData.Action.RUN_COMMAND, "/" + COMMAND_ALIAS + " " + APPROVE_SUBCOMAMND + " " + name));
        approveButton.setHoverData(new HoverData(HoverData.Action.SHOW_TEXT, TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixApproveButtonHover().replace("${prefix}", prefix).replace("${player}", name))));

        TextMessage denyButton = TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixDenyButton().replace("${prefix}", prefix)
                .replace("${player}", name));
        denyButton.setClickData(new ClickData(ClickData.Action.RUN_COMMAND, "/" + COMMAND_ALIAS + " " + DENY_SUBCOMMAND + " " + name));
        denyButton.setHoverData(new HoverData(HoverData.Action.SHOW_TEXT, TextMessage.fromColor(prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixDenyButtonHover().replace("${prefix}", prefix).replace("${player}", name))));


        return TextMessage.fromColor(
                prefixPlugin.getCoreConfig().getConfigData().getMessageLocale().getPrefixListMessage()
                .replace("${prefix}", prefix)
                .replace("${player}", name)
        ).addExtra(approveButton).addExtra(TextMessage.fromColor(" ").addExtra(denyButton));
    }

    private void applyPrefix(IFPlayer<?> player, String prefix) {
        prefixPlugin.getPrefixManager().addToAwait(player, prefix);
    }

}
