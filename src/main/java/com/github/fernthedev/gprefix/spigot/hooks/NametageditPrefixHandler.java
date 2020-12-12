package com.github.fernthedev.gprefix.spigot.hooks;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class NametageditPrefixHandler implements Listener {

    private static String prefixCommand(String player, String prefix) {
        return SpigotPlugin.getConfigData().getNameTagEditPrefixCommand()
                .replace("{player}", player)
                .replace("{prefix}", prefix);
    }

    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        if (prefixInfoData.getPrefixUpdateMode().approved()) {
            Player player = Bukkit.getPlayer(uuid);

            Universal.debug("Setting nametag prefix " + uuid + " " + (player != null));
            if (player != null)
                setPrefix(player, prefixInfoData);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        PrefixInfoData prefixInfoData = Core.getPrefixPlugin().getPrefixManager().getPrefixes().get(uuid);

        if (prefixInfoData != null && prefixInfoData.getPrefixUpdateMode().approved()) {

            setPrefix(e.getPlayer(), prefixInfoData);
        }
    }

    private void setPrefix(@NonNull Player player, PrefixInfoData prefixInfoData) {
        Universal.debug("Setting nametag prefix " + player.getUniqueId() + " ");

        String unformattedPrefix = prefixInfoData.getPrefix() + SpigotPlugin.getConfigData().getAppendPrefixRequestSuffix();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prefixCommand(player.getName(), unformattedPrefix));

        // TODO: Wait for NTE to add persistent API methods
        // as they currently only save to memory.
//
//        String prefix = ChatColor.translateAlternateColorCodes('&', unformattedPrefix);
//
//        INametagApi api = NametagEdit.getApi();
//
//        Nametag nametag = api.getNametag(player);
//        api.clearNametag(player);
//        api.setNametag(player, prefix, nametag.getSuffix());
//
//        api.applyTagToPlayer(player, true);
    }

}
