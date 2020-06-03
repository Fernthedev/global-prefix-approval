package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.database.DatabaseAuthInfo;
import com.github.fernthedev.fernapi.universal.mysql.DatabaseManager;


public class DBManager extends DatabaseManager {

    public DBManager(DatabaseAuthInfo authInfo) {
//        setDatabaseHandler(HikariDatabaseHandler.instance);
        connect(authInfo);
    }

    /**
     * This is called after you attempt a connection
     *
     * @param connected Returns true if successful
     * @see DatabaseManager#connect(DatabaseAuthInfo)
     */
    @Override
    public void onConnectAttempt(boolean connected) {
        if(connected) {
            Universal.getMethods().getLogger().info("Connected successfully");
        }else{
            Universal.getMethods().getLogger().warning("Unable to connect successfully");
        }
    }
}
