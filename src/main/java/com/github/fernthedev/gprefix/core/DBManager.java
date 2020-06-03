package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.database.DatabaseAuthInfo;
import com.github.fernthedev.fernapi.universal.data.database.HikariDatabaseAuthInfo;
import com.github.fernthedev.fernapi.universal.mysql.DatabaseManager;
import com.github.fernthedev.fernapi.universal.mysql.HikariDatabaseHandler;


public class DBManager extends DatabaseManager {

    public DBManager(HikariDatabaseAuthInfo authInfo) {
        setDatabaseHandler(new HikariDatabaseHandler(authInfo));
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
