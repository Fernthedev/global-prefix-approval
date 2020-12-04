package com.github.fernthedev.gprefix.velocity.hooks;

import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.hooks.LuckPermsPrefixCoreHandler;
import com.github.fernthedev.gprefix.velocity.event.PrefixUpdateEvent;
import com.velocitypowered.api.event.Subscribe;

import java.util.UUID;

public class LuckPermsPrefixHandler {

    @Subscribe
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        LuckPermsPrefixCoreHandler.prefix(uuid, prefixInfoData);
    }

}
