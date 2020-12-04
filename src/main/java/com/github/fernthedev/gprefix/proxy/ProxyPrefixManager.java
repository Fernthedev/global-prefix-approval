package com.github.fernthedev.gprefix.proxy;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.fernapi.universal.data.network.Channel;
import com.github.fernthedev.fernapi.universal.data.network.IServerInfo;
import com.github.fernthedev.fernapi.universal.data.network.PluginMessageData;
import com.github.fernthedev.fernapi.universal.handlers.PluginMessageHandler;
import com.github.fernthedev.gprefix.core.Channels;
import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixManager;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.message.PrefixListPluginData;
import com.github.fernthedev.gprefix.core.message.PrefixRequestPluginData;
import com.github.fernthedev.gprefix.core.message.PrefixUpdateData;
import lombok.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.fernthedev.gprefix.core.CommonNetwork.PrefixUpdateMode.AWAIT_APPROVAL;

public abstract class ProxyPrefixManager extends PluginMessageHandler implements PrefixManager {

    protected static final List<String> queuedServers = new ArrayList<>();

    public static void runPrefixListUpdate() {
        runPrefixListUpdate("ALL");
    }

    @Override
    public void updatePrefixStatus(FernCommandIssuer staff, IFPlayer<?> player, PrefixInfoData prefixInfoData, boolean silent) {
        PrefixManager.super.updatePrefixStatus(staff, player, prefixInfoData, silent);
        CommonNetwork.updatePlayerPrefixStatus(staff, player, prefixInfoData, silent);
    }

    public static void runPrefixListUpdate(String server) {
        List<UUID> uuids = new ArrayList<>(prefixes.keySet()).parallelStream()
                .filter(uuid -> prefixes.get(uuid).getPrefixUpdateMode() == AWAIT_APPROVAL)
                .collect(Collectors.toList());

        Map<UUID, PrefixInfoData> prefixMap = new HashMap<>();

        uuids.forEach(uuid -> prefixMap.put(uuid, prefixes.get(uuid)));

        Universal.getNetworkHandler().getServers().forEach(((s, serverInfo) -> {
            if (serverInfo.getPlayers().isEmpty()) queuedServers.add(serverInfo.getName());
        }));

        PrefixListPluginData prefixRequestData = new PrefixListPluginData(prefixMap, new ByteArrayOutputStream(), server, Channels.PREFIX_RELOAD, Channels.PREFIX_CHANNEL);

        Universal.debug("Sending prefixes to " + server + " prefixes: " + prefixRequestData.toString());

        Universal.getMessageHandler().sendPluginData(prefixRequestData);
    }

    /**
     * This is the channel name that will be registered incoming and outgoing
     *
     * @return The channels that will be incoming and outgoing
     */
    @Override
    public List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();
        channels.add(Channels.PREFIX_CHANNEL);
        return channels;
    }

    @Override
    public void onMessageReceived(PluginMessageData pluginMessageData, Channel channel) {
        if (pluginMessageData instanceof PrefixRequestPluginData) {
            Core.getPrefixPlugin().getStorageHandler().load();
            runPrefixListUpdate();
        }

        if (pluginMessageData instanceof PrefixListPluginData) {
            prefixes.clear();
            prefixes.putAll(((PrefixListPluginData) pluginMessageData).getPlayerPrefixData());
            Core.getPrefixPlugin().getStorageHandler().save();
            runPrefixListUpdate();
        }

        if (pluginMessageData instanceof PrefixUpdateData) {
            Core.getPrefixPlugin().getStorageHandler().load();
            PrefixUpdateData prefixUpdateData = (PrefixUpdateData) pluginMessageData;


            FernCommandIssuer fernCommandIssuer = prefixUpdateData.isStaffConsole()
                    ? Universal.getMethods().getConsoleAbstract()
                    : Universal.getMethods().getPlayerFromUUID(prefixUpdateData.getStaffUUID());

            updatePrefixStatus(fernCommandIssuer, Universal.getMethods().getPlayerFromUUID(prefixUpdateData.getPlayerUUID()), prefixUpdateData.getPrefixInfoData(), prefixUpdateData.isSilent());

//            prefixes.put(prefixUpdateData.getPlayerUUID(), prefixUpdateData.getPrefixInfoData());
//
//            if (!prefixUpdateData.isSilent()) {
//                @NonNull OfflineFPlayer<?> playerFromUUID = Universal.getMethods().getPlayerFromUUID(prefixUpdateData.getPlayerUUID());
//
//                if (playerFromUUID.isOnline()) {
//                    sendMail(playerFromUUID, prefixUpdateData.getPrefixInfoData());
//
//                }
//            }

//            Core.getPrefixPlugin().getStorageHandler().save();
            runPrefixListUpdate();

//            if (Universal.getMethods().getServerType() == ServerType.BUKKIT) {
//                try {
//                    String type = pluginMessageData.getProxyChannelType(); //TYPE
//                    String server = pluginMessageData.getServer(); // Server
//                    String subChannel = pluginMessageData.getSubChannel(); // Subchannel
//
//                    Queue<String> dataList = new LinkedList<>(pluginMessageData.getExtraData());
//
//                    if (subChannel.equalsIgnoreCase(Channels.PREFIX_RELOAD)) {
//                        String playerName = dataList.remove();
//                        String uuid = dataList.remove();
//
//
//                        BungeePlugin.getStorageHandler().load();
//
//                        String nick = null;
//
//                        for (RowData rowData : databaseInfo.getRowDataList()) {
//                            if (rowData.getColumn("PLAYERUUID").getValue() == null) continue;
//
//                            if (rowData.getColumn("PLAYERUUID").getValue().equals(uuid)) {
//                                nick = rowData.getColumn("PREFIX").getValue();
//                            }
//                        }
//
//
//                        SpigotPrefixManager.handleNick(uuid, playerName, nick);
//
//
//                    }
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    protected void updateServer(IServerInfo serverInfo) {
        Universal.debug("Sending to queued server " + serverInfo.getName());
        runPrefixListUpdate(serverInfo.getName());
        queuedServers.remove(serverInfo.getName());
    }

    public void handleSwitchServer(@NonNull IServerInfo serverInfo) {
        if (queuedServers.contains(serverInfo.getName())) {
            updateServer(serverInfo);
        }
    }

    public void handleOnJoin(@NonNull IServerInfo serverInfo, @NonNull IFPlayer<?> player) {
        if (prefixes.containsKey(player.getUniqueId())) {
            sendMail(Universal.getMethods().convertPlayerObjectToFPlayer(player), prefixes.get(player.getUniqueId()));
        }

        if (queuedServers.contains(serverInfo.getName())) {
            updateServer(serverInfo);
        }
    }
}