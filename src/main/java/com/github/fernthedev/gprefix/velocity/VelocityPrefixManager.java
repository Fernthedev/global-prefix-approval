package com.github.fernthedev.gprefix.velocity;


import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.proxy.ProxyPrefixManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;

public class VelocityPrefixManager extends ProxyPrefixManager {

    @Subscribe
    public void onSwitchServers(ServerPostConnectEvent e) {
        if (e.getPlayer().getCurrentServer().isPresent() && e.getPreviousServer() != null) {
            handleSwitchServer(Universal.getNetworkHandler().toServer(e.getPlayer().getCurrentServer()));
        }
    }

    @Subscribe
    public void onJoin(PlayerChooseInitialServerEvent event) {
        if (event.getInitialServer().isPresent()) {
            handleOnJoin(Universal.getNetworkHandler().toServer(event.getInitialServer()), Universal.getMethods().convertPlayerObjectToFPlayer(event.getPlayer()));
        }
    }






}
