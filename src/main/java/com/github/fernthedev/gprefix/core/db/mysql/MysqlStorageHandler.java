package com.github.fernthedev.gprefix.core.db.mysql;

import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.data.database.RowData;
import com.github.fernthedev.fernapi.universal.data.database.TableInfo;
import com.github.fernthedev.fernapi.universal.mysql.DatabaseListener;
import com.github.fernthedev.fernapi.universal.util.UUIDFetcher;
import com.github.fernthedev.gprefix.core.Core;
import com.github.fernthedev.gprefix.core.DBManager;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import com.github.fernthedev.gprefix.core.db.impl.StorageHandler;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class MysqlStorageHandler implements StorageHandler {


    //    private ScheduleTaskWrapper<?, ?> scheduleTaskWrapper;
    private static DatabaseListener databaseManager;
    private static PrefixDatabaseInfo databaseInfo;


    public void init() {
        Universal.getMethods().getAbstractLogger().info("Initializing MySQL");

        if (databaseManager == null) {
            databaseManager = new DBManager(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());
        }

        databaseManager.connect();
        setupTable();
        load();
        Universal.getMethods().getAbstractLogger().info("Finished MySQL");
    }

    @SneakyThrows
    private CompletableFuture<TableInfo<PrefixDatabaseInfo.PrefixRowDatabaseInfo>> setupTable() {
        if (databaseManager == null)
            databaseManager = new DBManager(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());


        databaseInfo = new PrefixDatabaseInfo();

        databaseManager.createTable(databaseInfo).get();
        return databaseInfo.loadFromDB(databaseManager);
    }

    public CompletableFuture<?> load() {
        if (databaseManager == null)
            databaseManager = new DBManager(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());


        return databaseInfo.loadFromDB(databaseManager).handle((prefixRowDatabaseInfoTableInfo, throwable) -> {

            Queue<RowData> rowDataStack = new LinkedList<>(databaseInfo.getRowDataListCopy().values());

            Core.getPrefixPlugin().getPrefixManager().getPrefixes().clear();

            Gson gson = new Gson();

            while (!rowDataStack.isEmpty()) {
                RowData rowData = rowDataStack.remove();

                UUID uuid = UUIDFetcher.uuidFromString(rowData.getColumn("PLAYERUUID").getValue());
                PrefixInfoData prefix = gson.fromJson(rowData.getColumn("PREFIX").getValue(), PrefixInfoData.class);

                Core.getPrefixPlugin().getPrefixManager().getPrefixes().put(uuid, prefix);
            }

            return prefixRowDatabaseInfoTableInfo;
        });


    }

    public CompletableFuture<?> save() {
        if (databaseManager == null)
            databaseManager = new DBManager(Core.getPrefixPlugin().getCoreConfig().getConfigData().getDatabaseAuthInfo());

        return Universal.getScheduler().runAsync(() -> {
            try {
                if (!databaseManager.isConnected()) {
                    throw new IllegalStateException("Could not connect to SQL DB");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            Core.getPrefixPlugin().getPrefixManager().getPrefixes()
                    .forEach((uuid, prefixInfoData) ->
                            databaseManager.removeRowIfColumnContainsValue(databaseInfo, "PLAYERUUID", uuid.toString())
                                    .thenRun(() -> setPlayer(uuid, prefixInfoData)));

            databaseInfo.loadFromDB(databaseManager).thenRun(() -> {
                Queue<PrefixDatabaseInfo.PrefixRowDatabaseInfo> rowDataStack = new LinkedList<>(databaseInfo.getRowDataListCopy().values());

                while (!rowDataStack.isEmpty()) {
                    PrefixDatabaseInfo.PrefixRowDatabaseInfo rowDataCheck = rowDataStack.remove();

                    UUID uuidCheck = rowDataCheck.getUuid();

                    if (!Core.getPrefixPlugin().getPrefixManager().getPrefixes().containsKey(uuidCheck))
                        databaseManager.removeRowIfColumnContainsValue(databaseInfo, "PLAYERUUID", uuidCheck.toString());
                }


            });


        }).getTaskFuture();
    }

    private void setPlayer(UUID uuid, PrefixInfoData prefixInfoData) {
        PrefixDatabaseInfo.PrefixRowDatabaseInfo rowData = new PrefixDatabaseInfo.PrefixRowDatabaseInfo(uuid, prefixInfoData);
        databaseManager.insertIntoTable(databaseInfo, rowData).handle((integer, throwable) -> {
            if (throwable != null)
                throwable.printStackTrace();

            return integer;
        });
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
