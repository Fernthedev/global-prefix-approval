package com.github.fernthedev.gprefix.core.db.mysql;

import com.github.fernthedev.fernapi.universal.data.database.RowData;
import com.github.fernthedev.fernapi.universal.data.database.TableInfo;
import com.github.fernthedev.gprefix.core.db.PrefixInfoData;
import lombok.Getter;
import org.panteleyev.mysqlapi.annotations.Column;
import org.panteleyev.mysqlapi.annotations.PrimaryKey;

import java.util.UUID;

public class PrefixDatabaseInfo extends TableInfo<PrefixDatabaseInfo.PrefixRowDatabaseInfo> {

    public PrefixDatabaseInfo() {
        super("fern_prefix", PrefixRowDatabaseInfo.class, PrefixRowDatabaseInfo::new);
    }

    @Getter
    public static class PrefixRowDatabaseInfo extends RowData {

        @Column("PLAYERUUID")
        @PrimaryKey(isAutoIncrement = false)
        private UUID uuid;

        @Column("PREFIX")
        private PrefixInfoData prefix;

        /**
         * Use to instantiate Row Data with empty data.
         * <p>
         * It is recommended to call {@link #initiateRowData()} after your values are instantiated.
         */
        public PrefixRowDatabaseInfo(UUID uuid, PrefixInfoData prefix) {
            super();
            this.uuid = uuid;
            this.prefix = prefix;
            initiateRowData();
        }

        @Deprecated
        public PrefixRowDatabaseInfo() {
            super();
        }
    }
}
