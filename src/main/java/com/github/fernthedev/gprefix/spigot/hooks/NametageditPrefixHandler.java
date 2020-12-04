package com.github.fernthedev.gprefix.spigot.hooks;

import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class NametageditPrefixHandler implements Listener {



    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        if (prefixInfoData.getPrefixUpdateMode().approved()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null)
                NametagEdit.getApi().setPrefix(player,
                        ChatColor.translateAlternateColorCodes('&', prefixInfoData.getPrefix() + SpigotPlugin.getConfigData().getAppendPrefixRequestSuffix())
                );
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        PrefixInfoData prefixInfoData = Core.getPrefixPlugin().getPrefixManager().getPrefixes().get(uuid);

        if (prefixInfoData != null && prefixInfoData.getPrefixUpdateMode().approved()) {
            String prefix = ChatColor.translateAlternateColorCodes('&', prefixInfoData.getPrefix() + SpigotPlugin.getConfigData().getAppendPrefixRequestSuffix());

            if (!NametagEdit.getApi().getNametag(e.getPlayer()).getPrefix().equals(prefix))
                NametagEdit.getApi().setPrefix(e.getPlayer(), prefix);
        }
    }

}
