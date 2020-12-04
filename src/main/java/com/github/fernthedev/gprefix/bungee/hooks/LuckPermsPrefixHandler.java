package com.github.fernthedev.gprefix.bungee.hooks;

import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.bungee.BungeePlugin;
import com.github.fernthedev.gprefix.bungee.event.PrefixUpdateEvent;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class LuckPermsPrefixHandler implements Listener {

    @EventHandler
    public void onPrefix(PrefixUpdateEvent e) {
        UUID uuid = e.getUuid();
        PrefixInfoData prefixInfoData = e.getPrefixInfoData();

        if (e.getPrefixInfoData().getPrefixUpdateMode().approved()) {
            User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
            if (user != null) {

                int priority = 1;
                for (Node node : user.data().toCollection()) {
                    if (node instanceof PrefixNode) {
                        priority = Math.max(((PrefixNode) node).getPriority() + 1, priority);
                    }
                }

                user.data().add(PrefixNode.builder()
                        .prefix(ChatColor.translateAlternateColorCodes('&', prefixInfoData.getPrefix() + BungeePlugin.getDataConfig().getConfigData().getAppendPrefixRequestSuffix()))
                        .priority(priority)
                        .build()
                );
            }
        }
    }

}
