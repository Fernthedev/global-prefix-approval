package com.github.fernthedev.gprefix.velocity;


import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.gprefix.bungee.event.PrefixUpdateEvent;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.proxy.ProxyPrefixManager;
import com.github.fernthedev.gprefix.spigot.event.PrefixListUpdateEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;

import java.util.Optional;

public class VelocityPrefixManager extends ProxyPrefixManager {

    @Override
    public void updatePrefixStatus(FernCommandIssuer staff, IFPlayer<?> player, PrefixInfoData prefixInfoData, boolean silent) {
        super.updatePrefixStatus(staff, player, prefixInfoData, silent);

        VelocityPlugin.getInstance().getServer().getEventManager().fireAndForget(new PrefixListUpdateEvent());

        VelocityPlugin.getInstance().getServer().getEventManager().fireAndForget(new PrefixUpdateEvent(player.getUuid(), prefixInfoData));
    }

    @Subscribe
    public void onSwitchServers(ServerPostConnectEvent e) {
        if (e.getPlayer().getCurrentServer().isPresent() && isPresent(e.getPreviousServer())) {
            handleSwitchServer(Universal.getNetworkHandler().toServer(e.getPlayer().getCurrentServer()));
        }
    }

    /**
     * Handle method that might change in the future
     * @param check
     * @return
     */
    private boolean isPresent(Object check) {
        if (check instanceof Optional) {
            return ((Optional<?>) check).isPresent();
        }

        return check != null;
    }

    @Subscribe
    public void onJoin(PlayerChooseInitialServerEvent event) {
        if (event.getInitialServer().isPresent()) {
            handleOnJoin(Universal.getNetworkHandler().toServer(event.getInitialServer()), Universal.getMethods().convertPlayerObjectToFPlayer(event.getPlayer()));
        }
    }






}
