package com.github.fernthedev.gprefix.core.message;

import com.github.fernthedev.fernapi.universal.data.network.Channel;
import com.github.fernthedev.fernapi.universal.data.network.IServerInfo;
import com.github.fernthedev.fernapi.universal.data.network.PluginMessageData;
import lombok.Getter;
import lombok.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;



public abstract class AbstractPrefixRequest extends PluginMessageData implements Serializable {

    @Getter
    protected RequestType requestType;

    /**
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public AbstractPrefixRequest(RequestType requestType, @NonNull ByteArrayOutputStream outputStream, IServerInfo server, String subChannel, Channel channel) {
        super(outputStream, server, subChannel, channel);
        this.requestType = requestType;
    }

    /**
     * @param outputStream The stream with data
     * @param server       The server to send to. Use server name or "ALL"
     * @param subChannel   The SubChannel to send to.
     * @param channel      The Plugin channel
     */
    public AbstractPrefixRequest(RequestType requestType, @NonNull ByteArrayOutputStream outputStream, String server, String subChannel, Channel channel) {
        super(outputStream, server, subChannel, channel);
        this.requestType = requestType;
    }

    public enum RequestType {
        REQUEST_LIST,
        UPDATE_PLAYER,
        RESPONSE_LIST
    }
}
