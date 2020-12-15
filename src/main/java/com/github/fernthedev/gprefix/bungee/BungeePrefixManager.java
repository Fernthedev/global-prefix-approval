package com.github.fernthedev.gprefix.bungee;


import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.gprefix.bungee.event.PrefixListUpdateEvent;
import com.github.fernthedev.gprefix.bungee.event.PrefixUpdateEvent;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.proxy.ProxyPrefixManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;

public class BungeePrefixManager extends ProxyPrefixManager implements Listener {

    @Override
    public void updatePrefixStatus(FernCommandIssuer staff, IFPlayer<?> player, PrefixInfoData prefixInfoData, boolean silent) {
        super.updatePrefixStatus(staff, player, prefixInfoData, silent);

        ProxyServer.getInstance().getPluginManager().callEvent(new PrefixListUpdateEvent());
        ProxyServer.getInstance().getPluginManager().callEvent(new PrefixUpdateEvent(player.getUuid(), prefixInfoData));
    }

    /**
     * Calls the events for each player update on the proxy
     *
     * @param playersToUpdate
     */
    @Override
    protected void callUpdateEvents(Map<UUID, PrefixInfoData> playersToUpdate) {
        ProxyServer.getInstance().getPluginManager().callEvent(new PrefixListUpdateEvent());

        playersToUpdate.forEach((uuid, prefixInfoData) ->
                ProxyServer.getInstance().getPluginManager()
                        .callEvent(new PrefixUpdateEvent(uuid, prefixInfoData)));
    }

    @EventHandler
    public void onSwitchServers(ServerSwitchEvent e) {
        if (e.getPlayer() != null && e.getPlayer().getServer() != null)
            handleSwitchServer(Universal.getNetworkHandler().toServer(e.getPlayer().getServer().getInfo()));
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        if (event.getPlayer() != null && event.getPlayer().getServer() != null) {
            handleOnJoin(Universal.getNetworkHandler().toServer(event.getPlayer().getServer().getInfo()),
                    Universal.getMethods().convertPlayerObjectToFPlayer(event.getPlayer())
            );
        }
    }





}
