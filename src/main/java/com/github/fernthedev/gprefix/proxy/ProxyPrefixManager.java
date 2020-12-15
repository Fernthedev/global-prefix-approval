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

    /**
     * Handle updating prefix
     * @param staff
     * @param player
     * @param prefixInfoData
     * @param silent
     */
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
            Map<UUID, PrefixInfoData> prefixes = PrefixManager.prefixes;
            Map<UUID, PrefixInfoData> prefixesCopy = new HashMap<>(prefixes);

            prefixes.clear();
            prefixes.putAll(((PrefixListPluginData) pluginMessageData).getPlayerPrefixData());
            Core.getPrefixPlugin().getStorageHandler().save();

            Map<UUID, PrefixInfoData> playersToUpdate = new HashMap<>();

            prefixes.forEach((uuid, prefixInfoData) -> {
                if (prefixesCopy.containsKey(uuid) && prefixesCopy.get(uuid) != prefixInfoData)
                    playersToUpdate.put(uuid, prefixInfoData);

                if (!prefixesCopy.containsKey(uuid))
                    playersToUpdate.put(uuid, prefixInfoData);
            });

            callUpdateEvents(playersToUpdate);


            runPrefixListUpdate();
        }

        if (pluginMessageData instanceof PrefixUpdateData) {
            Core.getPrefixPlugin().getStorageHandler().load();
            PrefixUpdateData prefixUpdateData = (PrefixUpdateData) pluginMessageData;


            FernCommandIssuer fernCommandIssuer = prefixUpdateData.isStaffConsole()
                    ? Universal.getMethods().getConsoleAbstract()
                    : Universal.getMethods().getPlayerFromUUID(prefixUpdateData.getStaffUUID());

            updatePrefixStatus(fernCommandIssuer, Universal.getMethods().getPlayerFromUUID(prefixUpdateData.getPlayerUUID()), prefixUpdateData.getPrefixInfoData(), prefixUpdateData.isSilent());

            runPrefixListUpdate();
        }
    }

    /**
     * Calls the events for each player update on the proxy
     * @param playersToUpdate
     */
    protected abstract void callUpdateEvents(Map<UUID, PrefixInfoData> playersToUpdate);

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
