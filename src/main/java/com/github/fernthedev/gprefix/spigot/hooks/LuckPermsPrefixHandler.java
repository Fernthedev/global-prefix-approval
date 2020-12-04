package com.github.fernthedev.gprefix.spigot.hooks;

import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.hooks.LuckPermsPrefixCoreHandler;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LuckPermsPrefixHandler implements Listener {

    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        LuckPermsPrefixCoreHandler.prefix(uuid, prefixInfoData);
    }

}
