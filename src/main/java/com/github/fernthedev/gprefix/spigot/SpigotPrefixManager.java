package com.github.fernthedev.gprefix.spigot;

import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.gprefix.core.CommonNetwork;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.PrefixManager;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.db.PluginMessagingDB;
import com.github.fernthedev.gprefix.spigot.event.PrefixListUpdateEvent;
import lombok.NonNull;
import org.bukkit.Bukkit;

public class SpigotPrefixManager implements PrefixManager {

    @Override
    public void updatePrefixStatus(FernCommandIssuer staff, IFPlayer<?> player, PrefixInfoData prefixInfoData, boolean silent) {
        PrefixManager.super.updatePrefixStatus(staff, player, prefixInfoData, silent);

        if (Core.getPrefixPlugin().getStorageHandler() instanceof PluginMessagingDB) {
            CommonNetwork.updatePlayerPrefixStatus(player, prefixInfoData, silent);
        }

        Core.getPrefixPlugin().getStorageHandler().save();

        Bukkit.getPluginManager().callEvent(new PrefixListUpdateEvent());
    }

    @Override
    public void sendMail(IFPlayer<?> player, @NonNull PrefixInfoData prefixInfoData) {
        if (!(Core.getPrefixPlugin().getStorageHandler() instanceof PluginMessagingDB)) {
            PrefixManager.super.sendMail(player, prefixInfoData);
        }
    }
}

