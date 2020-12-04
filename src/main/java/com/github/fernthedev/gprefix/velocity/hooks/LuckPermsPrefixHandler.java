package com.github.fernthedev.gprefix.velocity.hooks;

import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.velocity.VelocityPlugin;
import com.github.fernthedev.gprefix.velocity.event.PrefixUpdateEvent;
import com.velocitypowered.api.event.Subscribe;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;

import java.util.UUID;

public class LuckPermsPrefixHandler {

    @Subscribe
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
                    .prefix(ChatColor.translateAlternateColorCodes('&', prefixInfoData.getPrefix() + VelocityPlugin.getDataConfig().getConfigData().getAppendPrefixRequestSuffix()))
                    .priority(priority)
                    .build()
            );
        }
    }

}
