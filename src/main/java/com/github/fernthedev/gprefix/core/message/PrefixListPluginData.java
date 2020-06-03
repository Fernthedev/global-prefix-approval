package com.github.fernthedev.gprefix.core.message;

import com.github.fernthedev.fernapi.universal.data.network.Channel;
import com.github.fernthedev.fernapi.universal.data.network.IServerInfo;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import lombok.Getter;
import lombok.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

import static com.github.fernthedev.gprefix.core.Core.NAMESPACE;

@Getter
public class PrefixListPluginData extends AbstractPrefixRequest {

    public static final String GSON_NAME = NAMESPACE + "-responseListData";

    @Getter
    private Map<UUID, PrefixInfoData> playerPrefixData;


    /**
     * @param playerPrefixData
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public PrefixListPluginData(Map<UUID, PrefixInfoData> playerPrefixData, @NonNull ByteArrayOutputStream outputStream, IServerInfo server, String subChannel, Channel channel) {
        super(RequestType.RESPONSE_LIST, outputStream, server, subChannel, channel);
        this.playerPrefixData = playerPrefixData;
        updateFields();
    }

    /**
     * @param playerPrefixData
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public PrefixListPluginData(Map<UUID, PrefixInfoData> playerPrefixData, @NonNull ByteArrayOutputStream outputStream, String server, String subChannel, Channel channel) {
        super(RequestType.RESPONSE_LIST, outputStream, server, subChannel, channel);
        this.playerPrefixData = playerPrefixData;
        updateFields();
    }

    private void updateFields() {
        useGson = true;
        gsonName = GSON_NAME;
    }



}
