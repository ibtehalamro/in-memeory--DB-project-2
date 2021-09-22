package com.example.inmemoryweb.Repository;

import com.example.inmemoryweb.Exceptions.QueryExecutionException;
import com.example.inmemoryweb.databasestructure.*;
import com.example.inmemoryweb.filemanager.FileManager;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.UpdateContext;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

@Repository("UpdateRepository")
public class UpdateRepository implements UpdateDataRepository {

    private SchemaTablesContainer schemaTablesContainer;
    private FileManager fileManager;

    @Override
    public QueryResult executeQuery(Context context) {
        UpdateContext updateContext = (UpdateContext) context;
        Map<String, String> valuesWithColumns = updateContext.getValuesWithColumns();
        String tableName = updateContext.getSchemaTableName();
        SchemaTable schemaTable = schemaTablesContainer.getSchemaTables().get(tableName);
        List<Column> tableColumns = schemaTable.getColumns();
        Column whereColumnFromTable = getWhereColumnFromTable(tableColumns, updateContext.getWhere().getWhereColumnToken());
        BiPredicate<Value, Value> whereFilter = null;
        Value whereFilterValue = null;
        Where where = updateContext.getWhere();

        if (where != null) {
            whereFilter = where.generateWhereFilter(whereColumnFromTable);
            ValueFactory valueFactory = new ValueFactory(updateContext.getWhere().getWhereValueToken(), whereColumnFromTable);
            whereFilterValue = valueFactory.getObjectFromFactory();
        }

        List<DataRow> schemaTableRowsToBeUpdated = getSchemaTableRowsToBeUpdated(tableName, whereFilter, whereColumnFromTable, whereFilterValue);
        updateChildrenTables(updateContext, schemaTable, whereColumnFromTable, where, schemaTableRowsToBeUpdated);


        schemaTableRowsToBeUpdated.forEach(t -> updateRow(t, valuesWithColumns, tableName));

        QueryResult queryResult = new QueryResult(null);
        queryResult.setTableName(tableName);
        return queryResult;
    }

    private Column getWhereColumnFromTable(List<Column> tableColumns, String whereColumn) {
        return tableColumns.stream().filter(tableColumn -> tableColumn.getColumnName().equals(whereColumn)).findFirst().orElse(null);
    }
    private List<DataRow> getSchemaTableRowsToBeUpdated(String tableName, BiPredicate<Value, Value> whereFilter, Column whereColumnFromTable, Value whereFilterValue) {
        List<DataRow> rowsToUpdate;

        if (whereFilter == null) {
            rowsToUpdate = schemaTablesContainer.getSchemaTables().get(tableName).getAllRows();
        } else {
            rowsToUpdate = schemaTablesContainer.getSchemaTables().get(tableName).getAllRowsWithFilterApplied(whereFilter, whereColumnFromTable, whereFilterValue);
        }

        return rowsToUpdate;
    }


    private void updateChildrenTables(UpdateContext updateContext, SchemaTable schemaTable, Column whereColumnFromTable, Where where, List<DataRow> schemaTableRowsToBeUpdated) {
        List<String> childTablesNames = schemaTable.getTableConstraints().getChildTablesNames();
        String onUpdate = schemaTable.getTableConstraints().getOnUpdate();
        if (childTablesNames != null) {
            childTablesNames.forEach(
                    childTablesName -> {
                        SchemaTable childTable = schemaTablesContainer.getSchemaTables().get(childTablesName);
                        String foreignKeyColumn = childTable.getTableConstraints().getForeignKeyColumn();
                        DataRow columnFound = schemaTableRowsToBeUpdated.stream().filter(dataRow ->
                                dataRow.getTableRowMap().get(foreignKeyColumn) != null
                        ).findFirst().get();

                        switch (onUpdate) {
                            case "NO_ACTION":
                                throw new QueryExecutionException("can't update because of the child tables have values related");
                            case "CASCADE":
                                BiPredicate<Value, Value> whereFilterChild = where.generateWhereFilter(whereColumnFromTable);
                                Column childTableColumnByColumnName = childTable.getColumnByColumnName(childTable.getTableConstraints().getForeignKey());
                                List<DataRow> childTableRowsToBeUpdated = getSchemaTableRowsToBeUpdated(childTable.getTableName(), whereFilterChild, childTableColumnByColumnName, columnFound.getValueObjectByKey(foreignKeyColumn));
                                childTableRowsToBeUpdated.forEach(dataRow -> {
                                    if (dataRow.getTableRowMap().containsKey(childTableColumnByColumnName.getColumnName())) {
                                        String newValue = updateContext.getValuesWithColumns().get(foreignKeyColumn);
                                        dataRow.getTableRowMap().get(childTableColumnByColumnName.getColumnName()).setValue(Integer.valueOf(newValue));
                                    }
                                });

                                childTableRowsToBeUpdated.forEach(dataRow -> {
                                    Map<String, String> values = new HashMap<>();
                                    values.put("key", String.valueOf(dataRow.getValueObjectByKey("key").getValue()));
                                    updateRow(dataRow, values, childTablesName);
                                });
                        }
                    }
            );

        }
    }

    private void updateRow(DataRow rowFromTable, Map<String, String> valuesWithColumns, String tableName) {
        Map<String, Value> row = rowFromTable.getTableRowMap();
        SchemaTable table = schemaTablesContainer.getSchemaTables().get(tableName);
        Integer keyToPreviousRow = (Integer) row.get("key").getValue();

        valuesWithColumns.forEach((rowKey, rowValue) ->
                {
                    if (row.containsKey(rowKey)) {
                        Column tableColumn = schemaTablesContainer.getSchemaTables().get(tableName).getColumnByColumnName(rowKey);
                        ValueFactory valueFactory = new ValueFactory(valuesWithColumns.get(rowKey), tableColumn);
                        Value value = valueFactory.getObjectFromFactory();
                        rowFromTable.mapValueDataToTableColumn(value, rowKey);
                    }
                }
        );
        int key = (int) rowFromTable.getValueObjectByKey("key").getValue();
        synchronized (schemaTablesContainer.getSchemaTables().get(tableName)) {
            table.deleteRowFromTable(keyToPreviousRow);
            table.insertRow(key, rowFromTable);
            fileManager.updateRowIntoTableFile(tableName, keyToPreviousRow, key, rowFromTable);
        }
    }


    @Autowired
    public void setSchemaTablesContainer(SchemaTablesContainer schemaTablesContainer) {
        this.schemaTablesContainer = schemaTablesContainer;
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

}
