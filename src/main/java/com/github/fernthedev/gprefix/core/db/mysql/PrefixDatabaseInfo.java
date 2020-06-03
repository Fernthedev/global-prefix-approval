package com.github.fernthedev.gprefix.core.db.mysql;

import com.github.fernthedev.fernapi.universal.data.database.ColumnData;
import com.github.fernthedev.fernapi.universal.data.database.RowDataTemplate;
import com.github.fernthedev.fernapi.universal.data.database.TableInfo;

public class PrefixDatabaseInfo extends TableInfo {

    public static final RowDataTemplate ROW_DATA_TEMPLATE = new RowDataTemplate(
            new ColumnData("PLAYERUUID", ""),
            new ColumnData("PREFIX", ""));

    public PrefixDatabaseInfo() {
        super("fern_prefix", ROW_DATA_TEMPLATE);
    }
}
