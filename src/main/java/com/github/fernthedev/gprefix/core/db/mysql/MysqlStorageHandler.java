package com.github.fernthedev.gprefix.core.db.mysql;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.database.ColumnData;
import com.github.fernthedev.fernapi.universal.data.database.RowData;
import com.github.fernthedev.fernapi.universal.exceptions.database.DatabaseException;
import com.github.fernthedev.fernapi.universal.mysql.DatabaseListener;
import com.github.fernthedev.fernapi.universal.util.UUIDFetcher;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.DBManager;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.db.impl.StorageHandler;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;


public class MysqlStorageHandler implements StorageHandler {


    
//    private ScheduleTaskWrapper<?, ?> scheduleTaskWrapper;
    public static DatabaseListener DATABASE_MANAGER;
    private static PrefixDatabaseInfo databaseInfo;

    private void doSchedule() {
//        if (scheduleTaskWrapper != null) scheduleTaskWrapper.cancel();
//
//
//        scheduleTaskWrapper = Universal.getScheduler().runSchedule(() -> {
//            load();
//            save();
//        }, 45, 25, TimeUnit.SECONDS);
    }


    public void init() {
        Universal.getMethods().getLogger().info("Initializing MySQL");
        setupTable();
        doSchedule();
        load();
        Universal.getMethods().getLogger().info("Finished MySQL");
    }

    private void setupTable() {
        if (DATABASE_MANAGER == null)
            DATABASE_MANAGER = new DBManager(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());
        else DATABASE_MANAGER.connect(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());

        try {
            databaseInfo = new PrefixDatabaseInfo();
            DATABASE_MANAGER.createTable(databaseInfo);
            databaseInfo.getFromDatabase(DATABASE_MANAGER);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (DATABASE_MANAGER == null)
            DATABASE_MANAGER = new DBManager(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());
        else DATABASE_MANAGER.connect(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());

        doSchedule();
        DATABASE_MANAGER.runOnConnectAsync(() -> {
            try {
                databaseInfo.getFromDatabase(DATABASE_MANAGER);

                Queue<RowData> rowDataStack = new LinkedList<>(databaseInfo.getRowDataList());

                Core.getPrefixPlugin().getPrefixManager().getPrefixes().clear();

                Gson gson = new Gson();

                while(!rowDataStack.isEmpty()) {
                    RowData rowData = rowDataStack.remove();

                    UUID uuid = UUIDFetcher.uuidFromString(rowData.getColumn("PLAYERUUID").getValue());
                    PrefixInfoData prefix = gson.fromJson(rowData.getColumn("PREFIX").getValue(), PrefixInfoData.class);

                    Core.getPrefixPlugin().getPrefixManager().getPrefixes().put(uuid, prefix);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void save() {
        if (DATABASE_MANAGER == null)
            DATABASE_MANAGER = new DBManager(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());
        else DATABASE_MANAGER.connect(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());

        Gson gson = new Gson();
        doSchedule();

        DATABASE_MANAGER.runOnConnectAsync(() -> Core.getPrefixPlugin().getPrefixManager().getPrefixes().forEach((uuid, prefixInfoData) -> {
            try {
                DATABASE_MANAGER.removeRowIfColumnContainsValue(databaseInfo, "PLAYERUUID", uuid.toString());

                ColumnData playerColumn = new ColumnData("PLAYERUUID", uuid.toString());
                ColumnData prefixColumn = new ColumnData("PREFIX", gson.toJson(prefixInfoData));

                RowData rowData = new RowData(playerColumn, prefixColumn);
                DATABASE_MANAGER.insertIntoTable(databaseInfo, rowData);


                databaseInfo.getFromDatabase(DATABASE_MANAGER);
                Queue<RowData> rowDataStack = new LinkedList<>(databaseInfo.getRowDataList());

                while(!rowDataStack.isEmpty()) {
                    RowData rowDataCheck = rowDataStack.remove();

                    UUID uuidCheck = UUIDFetcher.uuidFromString(rowDataCheck.getColumn("PLAYERUUID").getValue());

                    if (!Core.getPrefixPlugin().getPrefixManager().getPrefixes().containsKey(uuidCheck))
                        DATABASE_MANAGER.removeRowIfColumnContainsValue(databaseInfo, "PLAYERUUID", uuidCheck.toString());
                }
            } catch (DatabaseException throwables) {
                throwables.printStackTrace();
            }
        }));
    }

//    @Getter
//    public class PrefixObject {
//        private String jsonObject;
//
//        public PrefixObject(Object jsonObject) {
//            this.jsonObject = jsonObject;
//        }
//    }
}
