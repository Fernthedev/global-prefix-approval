package com.github.fernthedev.gprefix.core.db;

import com.github.fernthedev.gprefix.core.CommonNetwork;

import java.io.Serializable;

public class PrefixInfoData implements Serializable {
    private String prefix;
    private CommonNetwork.PrefixUpdateMode prefixUpdateMode;

    public PrefixInfoData(String prefix, CommonNetwork.PrefixUpdateMode prefixUpdateMode) {
        this.prefix = prefix;
        this.prefixUpdateMode = prefixUpdateMode;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public CommonNetwork.PrefixUpdateMode getPrefixUpdateMode() {
        return this.prefixUpdateMode;
    }
}
