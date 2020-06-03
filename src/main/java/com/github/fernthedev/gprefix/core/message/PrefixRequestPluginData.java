package com.github.fernthedev.gprefix.core.message;

import com.github.fernthedev.fernapi.universal.data.network.Channel;
import com.github.fernthedev.fernapi.universal.data.network.IServerInfo;
import lombok.NonNull;

import java.io.ByteArrayOutputStream;

import static com.github.fernthedev.gprefix.core.Core.NAMESPACE;

public class PrefixRequestPluginData extends AbstractPrefixRequest {
    public static final String GSON_NAME = NAMESPACE + "-requestData";

    /**
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public PrefixRequestPluginData(@NonNull ByteArrayOutputStream outputStream, IServerInfo server, String subChannel, Channel channel) {
        super(RequestType.REQUEST_LIST, outputStream, server, subChannel, channel);
        updateFields();
    }

    /**
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public PrefixRequestPluginData(@NonNull ByteArrayOutputStream outputStream, String server, String subChannel, Channel channel) {
        super(RequestType.REQUEST_LIST, outputStream, server, subChannel, channel);
        updateFields();
    }

    private void updateFields() {
        useGson = true;
        gsonName = GSON_NAME;
    }


}
