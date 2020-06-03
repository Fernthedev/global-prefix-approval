package com.github.fernthedev.gprefix.bungee;

import com.github.fernthedev.gprefix.bungee.locale.BungeeMessageLocale;
import com.github.fernthedev.gprefix.core.CommonConfigData;
import com.github.fernthedev.gprefix.core.db.DBUtil;
import lombok.Getter;

public class BungeeConfigData extends CommonConfigData {
    public BungeeConfigData() {
//        if (token == null || token.isEmpty()) {
//            token = UUID.randomUUID().toString();
//        }
    }


    @Getter
    private BungeeMessageLocale bungeeMessageLocale = new BungeeMessageLocale();







}
