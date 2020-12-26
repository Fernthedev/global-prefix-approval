package com.github.fernthedev.gprefix.core;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.database.DatabaseAuthInfo;
import com.github.fernthedev.fernapi.universal.mysql.AikarFernDatabase;
import com.github.fernthedev.fernapi.universal.mysql.DatabaseListener;


public class DBManager extends DatabaseListener {

    public DBManager(DatabaseAuthInfo authInfo) {
        super(AikarFernDatabase.createHikariDatabase(Universal.getPlugin(), authInfo));
    }

    /**
     * This is called after you attempt a connection
     *
     * @param connected Returns true if successful
     * @see DatabaseListener#connect()
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
