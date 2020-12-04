package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.fernapi.universal.api.OfflineFPlayer;
import com.github.fernthedev.fernapi.universal.data.chat.TextMessage;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.github.fernthedev.gprefix.core.CommonNetwork.PrefixUpdateMode.AWAIT_APPROVAL;

public interface PrefixManager {

    Map<UUID, PrefixInfoData> prefixes = new HashMap<>();

    /**
     * Handles updating the prefix
     * in the database
     *
     * @param staff
     * @param player
     * @param prefixInfoData
     * @param silent
     */
    default void updatePrefixStatus(@Nullable FernCommandIssuer staff, IFPlayer<?> player, PrefixInfoData prefixInfoData, boolean silent) {
        prefixes.put(player.getUniqueId(), prefixInfoData);

        if (prefixInfoData.getPrefixUpdateMode() != AWAIT_APPROVAL) {
            if (!silent) {
                @NonNull OfflineFPlayer<?> playerFromUUID = Universal.getMethods().getPlayerFromUUID(player.getUniqueId());

                if (playerFromUUID.isOnline()) {
                    sendMail(player, Core.getPrefixPlugin().getPrefixManager().getPrefixes().get(player.getUniqueId()));
                    prefixes.remove(player.getUniqueId());
                }
            } else {
                prefixes.remove(player.getUniqueId());
            }

        }

        Core.getPrefixPlugin().getStorageHandler().save();
        if (prefixInfoData.getPrefixUpdateMode() == AWAIT_APPROVAL) {
            Core.getDateLogger().writeLog(player, prefixInfoData);
        } else {
            Core.getDateLogger().writeLog(Objects.requireNonNull(staff), player, prefixInfoData);
        }
    }


    default void updatePrefixStatus(@Nullable FernCommandIssuer staff, IFPlayer<?> player, CommonNetwork.PrefixUpdateMode prefixUpdateMode, boolean silent) {
        updatePrefixStatus(staff, player, new PrefixInfoData(prefixes.get(player.getUniqueId()).getPrefix(), prefixUpdateMode), silent);
    }

    default Map<UUID, String> getPrefixQueueMap() {
        Map<UUID, String> uuidStringMap = new HashMap<>();

        getPrefixes().forEach((uuid, prefixInfoData) -> {
            if (prefixInfoData.getPrefixUpdateMode() == CommonNetwork.PrefixUpdateMode.AWAIT_APPROVAL) {
                uuidStringMap.put(uuid ,prefixInfoData.getPrefix());
            }
        });

        return uuidStringMap;
    }

    default Map<UUID, PrefixInfoData> getPrefixes() {
        return prefixes;
    }

    default void sendMail(IFPlayer<?> player, @NonNull PrefixInfoData prefixInfoData) {
        String msg = null;

        switch (prefixInfoData.getPrefixUpdateMode()) {
            case APPROVED:
                msg = Core.getPrefixPlugin().getCoreConfig().getConfigData().getMessageLocale().getApprovedPrefixMail();
                break;
            case DENIED:
                msg = Core.getPrefixPlugin().getCoreConfig().getConfigData().getMessageLocale().getDeniedPrefixMail();
                break;
            case AWAIT_APPROVAL:
                prefixes.put(player.getUniqueId(), new PrefixInfoData(prefixInfoData.getPrefix(), AWAIT_APPROVAL));
                break;
        }

        if (msg != null) {
            player.sendMessage(TextMessage.fromColor(msg.replace("${player}", player.getName()).replace("${prefix}", prefixInfoData.getPrefix())));
            prefixes.remove(player.getUniqueId());
        }
    }

    default void addToAwait(IFPlayer<?> player, String newNick) {
        updatePrefixStatus(null, player, new PrefixInfoData(newNick, CommonNetwork.PrefixUpdateMode.AWAIT_APPROVAL), true);
    }
}
