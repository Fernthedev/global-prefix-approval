package com.github.fernthedev.gprefix.bungee.hooks;

import com.github.fernthedev.gprefix.bungee.event.PrefixUpdateEvent;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.hooks.LuckPermsPrefixCoreHandler;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class LuckPermsPrefixHandler implements Listener {

    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        LuckPermsPrefixCoreHandler.prefix(uuid, prefixInfoData);


    }

}
