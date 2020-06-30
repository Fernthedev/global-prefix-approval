package com.github.fernthedev.gprefix.core.message;

import com.github.fernthedev.fernapi.universal.api.FernCommandIssuer;
import com.github.fernthedev.fernapi.universal.api.IFPlayer;
import com.github.fernthedev.fernapi.universal.data.network.Channel;
import com.github.fernthedev.fernapi.universal.data.network.IServerInfo;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.github.fernthedev.gprefix.core.Core.NAMESPACE;

@Getter
public class PrefixUpdateData extends AbstractPrefixRequest {

    public static final String GSON_NAME = NAMESPACE + "-updateData";

    private PrefixInfoData prefixInfoData;
    private UUID playerUUID;

    @Nullable
    private UUID staffUUID = null;
    private boolean isStaffConsole = false;

    @Setter
    private boolean silent = false;

    /**
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public PrefixUpdateData(UUID playerUUID,  PrefixInfoData prefixInfoData, @NonNull ByteArrayOutputStream outputStream, IServerInfo server, String subChannel, Channel channel) {
        super(RequestType.UPDATE_PLAYER, outputStream, server, subChannel, channel);
        this.prefixInfoData = prefixInfoData;
        this.playerUUID = playerUUID;

        updateFields();
    }

    /**
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public PrefixUpdateData(UUID playerUUID, PrefixInfoData prefixInfoData, @NonNull ByteArrayOutputStream outputStream, String server, String subChannel, Channel channel) {
        super(RequestType.UPDATE_PLAYER, outputStream, server, subChannel, channel);
        this.prefixInfoData = prefixInfoData;
        this.playerUUID = playerUUID;
        updateFields();
    }

    public PrefixUpdateData setStaffUUID(@NonNull FernCommandIssuer fernCommandIssuer) {
        if (fernCommandIssuer instanceof IFPlayer<?> || fernCommandIssuer.isPlayer()) {
            this.staffUUID = fernCommandIssuer.getUniqueId();
            isStaffConsole = false;
        } else {
            staffUUID = null;
            isStaffConsole = true;
        }
        return this;
    }

    private void updateFields() {
        useGson = true;
        gsonName = GSON_NAME;
    }



}
