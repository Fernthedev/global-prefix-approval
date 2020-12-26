package com.github.fernthedev.gprefix.spigot;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixManager;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.db.PluginMessagingDB;
import com.github.fernthedev.gprefix.spigot.event.PrefixListUpdateEvent;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.UUID;

public class SpigotPrefixManager implements PrefixManager {
//
//    private boolean
//
    @Override
    public void updatePrefixStatus(FernCommandIssuer staff, IFPlayer<?> player, PrefixInfoData prefixInfoData, boolean silent) {
        PrefixManager.super.updatePrefixStatus(staff, player, prefixInfoData, silent);

        if (Core.getPrefixPlugin().getStorageHandler() instanceof PluginMessagingDB) {
            CommonNetwork.updatePlayerPrefixStatus(staff, player, prefixInfoData, silent);
        }

        Bukkit.getPluginManager().callEvent(new PrefixListUpdateEvent());


        applyPrefix(player.getUniqueId(), prefixInfoData);
    }

    @Override
    public void sendMail(IFPlayer<?> player, @NonNull PrefixInfoData prefixInfoData) {
        if (!(Core.getPrefixPlugin().getStorageHandler() instanceof PluginMessagingDB)) {
            PrefixManager.super.sendMail(player, prefixInfoData);
        }
    }

    public void updatePrefixStatus(UUID playerUUID, PrefixInfoData prefixInfoData) {
        Bukkit.getPluginManager().callEvent(new PrefixListUpdateEvent());

        applyPrefix(playerUUID, prefixInfoData);
    }

    private void applyPrefix(UUID uuid, PrefixInfoData prefixInfoData) {
        Universal.debug(ChatColor.GOLD + "Setting player prefix for " + uuid);

        Bukkit.getPluginManager().callEvent(new PrefixUpdateEvent(uuid, prefixInfoData));

    }
}

