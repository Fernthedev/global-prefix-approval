package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.data.database.DatabaseAuthInfo;
import com.github.fernthedev.fernapi.universal.mysql.HikariSQLDriver;
import com.github.fernthedev.gprefix.core.db.DBUtil;
import com.github.fernthedev.gprefix.core.locale.MessageLocale;
import lombok.Getter;

@Getter
public class CommonConfigData {

    private boolean debugMode = true;
    private MessageLocale messageLocale = new MessageLocale();

    private int prefixLength = 16;
    private boolean includeColorCodesInLength = false;

    @Getter
    private DatabaseAuthInfo databaseAuthInfo = new DatabaseAuthInfo(
            "root",
            "pass",
            "3306",
            "localhost",
            "database",
            HikariSQLDriver.MARIADB_DRIVER);

    @Getter
    private DBUtil.StorageTypes storageTypes = DBUtil.StorageTypes.JSON;


    private boolean writeLogs = true;
//
//    @Getter
//    protected String token;

}
