package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.message.PrefixUpdateData;
import lombok.NonNull;

import java.io.ByteArrayOutputStream;

public class CommonNetwork {

    public enum PrefixUpdateMode {
        AWAIT_APPROVAL,
        APPROVED,
        DENIED
    }

    @Deprecated
    public static void updatePlayerPrefixStatus(@NonNull IFPlayer<?> ifPlayer,  PrefixInfoData prefixInfoData) {
        Universal.debug("Updating prefixes for " + ifPlayer.getName());
        PrefixUpdateData abstractPrefixRequest = new PrefixUpdateData(ifPlayer.getUuid(), prefixInfoData, new ByteArrayOutputStream(), "ALL", Channels.PREFIX_RELOAD, Channels.PREFIX_CHANNEL);
        Universal.getMessageHandler().sendPluginData(abstractPrefixRequest);
    }

    @Deprecated
    public static void updatePlayerPrefixStatus(@NonNull IFPlayer<?> ifPlayer, PrefixInfoData prefixInfoData, boolean silent) {
        Universal.debug("Updating prefixes for " + ifPlayer.getName());
        PrefixUpdateData abstractPrefixRequest = new PrefixUpdateData(ifPlayer.getUuid(), prefixInfoData, new ByteArrayOutputStream(), "ALL", Channels.PREFIX_RELOAD, Channels.PREFIX_CHANNEL);
        abstractPrefixRequest.setSilent(silent);
        Universal.getMessageHandler().sendPluginData(abstractPrefixRequest);
    }

    public static void updatePlayerPrefixStatus(FernCommandIssuer staff, @NonNull IFPlayer<?> ifPlayer, PrefixInfoData prefixInfoData, boolean silent) {
        Universal.debug("Updating prefixes for " + ifPlayer.getName());
        PrefixUpdateData abstractPrefixRequest = new PrefixUpdateData(ifPlayer.getUuid(), prefixInfoData, new ByteArrayOutputStream(), "ALL", Channels.PREFIX_RELOAD, Channels.PREFIX_CHANNEL);

        if (staff != null) abstractPrefixRequest.setStaffUUID(staff);

        abstractPrefixRequest.setSilent(silent);
        Universal.getMessageHandler().sendPluginData(abstractPrefixRequest);
    }

}
