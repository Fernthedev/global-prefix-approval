package com.github.fernthedev.gprefix.core.hooks;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LuckPermsPrefixCoreHandler {
    private LuckPermsPrefixCoreHandler() {}

    public static void prefix(UUID uuid, PrefixInfoData prefixInfoData) {
        if (!prefixInfoData.getPrefixUpdateMode().approved()) {
            return;
        }

        Runnable runnable = () -> {
            UserManager userManager = LuckPermsProvider.get().getUserManager();

            User user;
            try {
                user = userManager.loadUser(uuid).get();
            } catch (InterruptedException e) {
                e.printStackTrace();

                if (!Universal.getMethods().isMainThread())
                    Thread.currentThread().interrupt();

                return;
            } catch (ExecutionException e) {
                e.printStackTrace();

                return;
            }

            if (user == null) {
                return;
            }

            int priority = 1;
            for (Node node : user.data().toCollection()) {
                if (node instanceof PrefixNode prefixNode) {
                    priority = Math.max(prefixNode.getPriority() + 1, priority);
                }
            }

            user.data().add(PrefixNode.builder()
                    .prefix(ChatColor.translateAlternateColorCodes('&', prefixInfoData.getPrefix() + Core.getPrefixPlugin().getCoreConfig().getConfigData().getAppendPrefixRequestSuffix()))
                    .priority(priority)
                    .build()
            );

            userManager.saveUser(user);
        };


        if (Universal.getMethods().isMainThread()) {
            Universal.getScheduler().runAsync(runnable);
        } else {
            runnable.run();
        }
    }
}
