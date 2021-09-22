package com.example.inmemoryweb.Repository;

import com.example.inmemoryweb.databasestructure.*;
import com.example.inmemoryweb.filemanager.FileManager;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.InsertContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("InsertRepository")
public class InsertRepository implements InsertDataRepository {
    private SchemaTablesContainer schemaTablesContainer;
    private FileManager fileManager;

    @Autowired
    public void setSchemaTablesContainer(SchemaTablesContainer schemaTablesContainer) {
        this.schemaTablesContainer = schemaTablesContainer;
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public QueryResult executeQuery(Context context) {
        InsertContext insertContext = (InsertContext) context;
        String tableName = insertContext.getSchemaTableName();
        SchemaTable table = schemaTablesContainer.getSchemaTables().get(tableName);
        DataRow dataRow = generateDataRow(tableName, insertContext.getQueryColumns(), insertContext.getQueryValues());
        int key = getKeyValue(tableName, dataRow);
        synchronized (schemaTablesContainer.getSchemaTables().get(tableName)) {
            table.insertRow(key, dataRow);
            insertRowIntoTableFile(tableName, dataRow, key);
        }
        QueryResult queryResult = new QueryResult(null);
        queryResult.setTableName(tableName);
        return queryResult;
    }
    private DataRow generateDataRow(String tableName, List<String> queryColumns, List<String> queryValues) {
        DataRow row = new DataRow();
        boolean isAllColumnsSelected = queryColumns.get(0).equals("*");
        List<Column> columnList = schemaTablesContainer.getSchemaTables().get(tableName).getColumns();

        if (isAllColumnsSelected) {
            row = row.getDataRowForAllColumns(columnList, queryValues);
        } else {
            row = row.getDataRowForSpecificColumns(columnList, queryColumns, queryValues);
        }
        return row;
    }
    private int getKeyValue(String tableName, DataRow dataRow) {
        int key;
        if (dataRow.getTableRowMap().get("key").getClass() == NullValue.class) {
            key = generateKey(tableName);
        } else {
            key = (int) dataRow.getTableRowMap().get("key").getValue();
        }
        return key;
    }
    private int generateKey(String tableName) {
        SchemaTable schemaTable = schemaTablesContainer.getSchemaTables().get(tableName);
        return schemaTable.keyGenerator();
    }
    private void insertRowIntoTableFile(String tableName, DataRow dataRow, int key) {
        fileManager.insertRowIntoTableFile(tableName, key, dataRow);
    }


}
