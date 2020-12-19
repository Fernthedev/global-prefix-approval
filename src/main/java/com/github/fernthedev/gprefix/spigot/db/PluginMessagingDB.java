package com.github.fernthedev.gprefix.spigot.db;

import com.github.fernthedev.fernapi.universal.FernAPIChannels;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.network.Channel;
import com.github.fernthedev.fernapi.universal.data.network.PluginMessageData;
import com.github.fernthedev.fernapi.universal.handlers.PluginMessageHandler;
import com.github.fernthedev.fernapi.universal.util.ProxyAskPlaceHolder;
import com.github.fernthedev.gprefix.core.Channels;
import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixManager;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.db.impl.StorageHandler;
import com.github.fernthedev.gprefix.core.message.PrefixListPluginData;
import com.github.fernthedev.gprefix.core.message.PrefixRequestPluginData;
import com.github.fernthedev.gprefix.core.message.PrefixUpdateData;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import com.github.fernthedev.gprefix.spigot.event.PrefixListUpdateEvent;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class PluginMessagingDB extends PluginMessageHandler implements StorageHandler, Listener {

    private static boolean firstRun = false;

    public PluginMessagingDB() {

    }

    /**
     * This is the channel name that will be registered incoming and outgoing
     * This is where you specify the channels you want to listen to
     * Just make a new {@link ArrayList} with Channel instance instance and add an instance of the channel accordingly.
     *
     * @return The channels that will be incoming and outgoing
     * @see ProxyAskPlaceHolder as an example
     */
    @Override
    public @NonNull List<Channel> getChannels() {
        return new ArrayList<>(Collections.singleton(Channels.PREFIX_CHANNEL));
    }

    /**
     * The event called when message is received from the channels registered
     *
     * @param data    The dataInfo received for use of the event.
     * @param channel The channel it was received from, for use of multiple channels in one listener
     */
    @Override
    public void onMessageReceived(PluginMessageData data, Channel channel) {
        Universal.debug("Plugin data: " + data);
        if (data instanceof PrefixListPluginData) {
            PrefixListPluginData prefixListPluginData = (PrefixListPluginData) data;
            Map<UUID, PrefixInfoData> prefixes = Core.getPrefixPlugin().getPrefixManager().getPrefixes();

            Map<UUID, PrefixInfoData> prefixesCopy = new HashMap<>(prefixes);

            prefixes.clear();
            prefixes.putAll(prefixListPluginData.getPlayerPrefixData());

            Universal.debug("Prefix list " + prefixListPluginData.getPlayerPrefixData());


            Map<UUID, PrefixInfoData> playersToUpdate = new HashMap<>();

            PrefixManager.prefixes.forEach((uuid, prefixInfoData) -> {
                if (prefixesCopy.containsKey(uuid) && prefixesCopy.get(uuid) != prefixInfoData)
                    playersToUpdate.put(uuid, prefixInfoData);

                if (!prefixesCopy.containsKey(uuid))
                    playersToUpdate.put(uuid, prefixInfoData);
            });

            callUpdateEvents(playersToUpdate);


        }

        if (data instanceof PrefixUpdateData) {
            PrefixUpdateData prefixUpdateData = (PrefixUpdateData) data;

            Universal.debug("Prefix update " + prefixUpdateData.getPlayerUUID() + " " + prefixUpdateData.getPrefixInfoData().getPrefix());

            if (prefixUpdateData.getPrefixInfoData().getPrefixUpdateMode() != CommonNetwork.PrefixUpdateMode.AWAIT_APPROVAL) {
                Core.getPrefixPlugin().getPrefixManager().getPrefixes().remove(prefixUpdateData.getPlayerUUID());
            } else {
                Core.getPrefixPlugin().getPrefixManager().getPrefixes().put(prefixUpdateData.getPlayerUUID(), prefixUpdateData.getPrefixInfoData());
            }

            SpigotPlugin.getInstance().getPrefixManager().updatePrefixStatus(prefixUpdateData.getPlayerUUID(), prefixUpdateData.getPrefixInfoData());
        }
    }

    private void callUpdateEvents(Map<UUID, PrefixInfoData> playersToUpdate) {

        Bukkit.getPluginManager().callEvent(new PrefixListUpdateEvent());
        playersToUpdate.forEach((uuid, prefixInfoData) -> Bukkit.getPluginManager().callEvent(new PrefixUpdateEvent(uuid, prefixInfoData)));


    }

    public static void runPrefixRequest() {
        Universal.debug("Requesting prefixes");
        firstRun = true;
        PrefixRequestPluginData prefixRequestPluginData = new PrefixRequestPluginData(new ByteArrayOutputStream(), FernAPIChannels.BUNGEECORD_PROXY_NAME, Channels.PREFIX_RELOAD, Channels.PREFIX_CHANNEL);

        Universal.getMessageHandler().sendPluginData(prefixRequestPluginData);
    }



    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        if (!firstRun)
            runPrefixRequest();
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotPlugin.getInstance());
        Universal.getMessageHandler().registerMessageHandler(this);

        if (!Bukkit.getOnlinePlayers().isEmpty())
            load();
    }

    @Override
    public void save() {
        PrefixListPluginData prefixListPluginData = new PrefixListPluginData(Core.getPrefixPlugin().getPrefixManager().getPrefixes(), new ByteArrayOutputStream(), "BungeeCord", Channels.PREFIX_RELOAD, Channels.PREFIX_CHANNEL);
        Universal.getMessageHandler().sendPluginData(prefixListPluginData);
    }

    @Override
    public void load() {
        runPrefixRequest();
    }
}
