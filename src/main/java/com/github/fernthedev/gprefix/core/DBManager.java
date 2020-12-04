package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.database.DatabaseAuthInfo;
import com.github.fernthedev.fernapi.universal.mysql.DatabaseListener;
import com.github.fernthedev.fernapi.universal.mysql.HikariDatabaseHandler;


public class DBManager extends DatabaseListener {

    public DBManager(DatabaseAuthInfo authInfo) {
        setDatabaseHandler(new HikariDatabaseHandler());
        connect(authInfo);
    }

    /**
     * This is called after you attempt a connection
     *
     * @param connected Returns true if successful
     * @see DatabaseListener#connect(DatabaseAuthInfo)
     */
    @Override
    public void onConnectAttempt(boolean connected) {
        if(connected) {
            Universal.getMethods().getAbstractLogger().info("Connected successfully");
        }else{
            Universal.getMethods().getAbstractLogger().warn("Unable to connect successfully");
        }
    }
}
