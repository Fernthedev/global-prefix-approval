package com.github.fernthedev.gprefix.spigot.hooks;

import com.github.fernthedev.fernapi.server.spigot.pluginhandlers.VaultHandler;
import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class VaultPrefixHandler implements Listener {

    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        try {
            if (SpigotPlugin.getInstance().getVaultHandler() == null) throw new IllegalStateException("Vault handler is null");
            if (VaultHandler.getChat() == null) SpigotPlugin.getInstance().getVaultHandler().hook();
        } catch (RuntimeException ee) {
            ee.printStackTrace();
        }

        VaultHandler.getChat().setPlayerPrefix(null, offlinePlayer, ChatColor.translateAlternateColorCodes('&', prefixInfoData.getPrefix() + SpigotPlugin.getConfigData().getAppendPrefixRequestSuffix()));
    }

}
