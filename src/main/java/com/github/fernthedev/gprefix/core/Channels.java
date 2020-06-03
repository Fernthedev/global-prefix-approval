package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.data.network.Channel;

public class Channels {


    private Channels() {
    }

    public static final Channel PREFIX_CHANNEL = new Channel("fern_globalprefix", "prefix", Channel.ChannelAction.BOTH);
    public static final String PREFIX_RELOAD = "PrefixReloadSQL";

}
