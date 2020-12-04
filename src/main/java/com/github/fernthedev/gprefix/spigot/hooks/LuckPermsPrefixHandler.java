package com.github.fernthedev.gprefix.spigot.hooks;

import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.spigot.SpigotPlugin;
import com.github.fernthedev.gprefix.spigot.event.PrefixUpdateEvent;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LuckPermsPrefixHandler implements Listener {

    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
        if (user != null) {

            int priority = 1;
            for (Node node : user.data().toCollection()) {
                if (node instanceof PrefixNode) {
                    priority = Math.max(((PrefixNode) node).getPriority() + 1, priority);
                }
            }

            user.data().add(PrefixNode.builder()
                    .prefix(ChatColor.translateAlternateColorCodes('&', prefixInfoData.getPrefix() + SpigotPlugin.getConfigData().getAppendPrefixRequestSuffix()))
                    .priority(priority)
                    .build()
            );
        }
    }

}
