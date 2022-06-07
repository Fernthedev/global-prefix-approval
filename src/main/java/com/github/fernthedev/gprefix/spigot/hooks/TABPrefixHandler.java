package com.github.fernthedev.gprefix.spigot.hooks;

import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import me.neznamy.tab.api.TabAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class TABPrefixHandler implements Listener {

    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        if (e.getPrefixInfoData().getPrefixUpdateMode() != CommonNetwork.PrefixUpdateMode.APPROVED) return;

        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        var player = TabAPI.getInstance().getPlayer(uuid);
        TabAPI.getInstance().getTeamManager().setPrefix(player, prefixInfoData.getPrefix());
    }

}
