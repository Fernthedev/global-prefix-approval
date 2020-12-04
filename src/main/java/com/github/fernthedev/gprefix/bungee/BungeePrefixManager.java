package com.github.fernthedev.gprefix.bungee;


import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.proxy.ProxyPrefixManager;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeePrefixManager extends ProxyPrefixManager implements Listener {

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
