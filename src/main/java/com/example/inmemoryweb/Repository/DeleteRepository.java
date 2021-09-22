package com.example.inmemoryweb.Repository;

import com.example.inmemoryweb.Exceptions.QueryExecutionException;
import com.example.inmemoryweb.databasestructure.*;
import com.example.inmemoryweb.filemanager.FileManager;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.DeleteContext;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.BiPredicate;

@Repository("DeleteRepository")
public class DeleteRepository implements DeleteDataRepository {

    private SchemaTablesContainer schemaTablesContainer;
    private FileManager fileManager;

    @Override
    public QueryResult executeQuery(Context context) {
        DeleteContext deleteContext = (DeleteContext) context;
        String tableName = deleteContext.getSchemaTableName();
        Where where = deleteContext.getWhere();
        SchemaTable schemaTable = schemaTablesContainer.getSchemaTables().get(tableName);
        List<Column> tableColumns = schemaTable.getColumns();
        List<DataRow> rowsToBeDeleted = getRowsToBeDeleted(tableName, where, schemaTable, tableColumns);

        executeTableConstraints(where, schemaTable, tableColumns, rowsToBeDeleted);
        deleteRowsFromQuery(schemaTable, rowsToBeDeleted);
        QueryResult queryResult = new QueryResult(null);
        queryResult.setTableName(tableName);
        return queryResult;
    }
    private List<DataRow> getRowsToBeDeleted(String tableName, Where where, SchemaTable schemaTable, List<Column> tableColumns) {
        List<DataRow> rowsToBeDeleted;
        if (where == null) {
            rowsToBeDeleted = schemaTable.getAllRows();
        } else {
            rowsToBeDeleted = getRowsToBeDeletedWithWhere(tableName, where, tableColumns);
        }
        return rowsToBeDeleted;
    }
    private List<DataRow> getRowsToBeDeletedWithWhere(String tableName, Where where, List<Column> tableColumns) {
        Column whereColumnFromTable = where.getWhereColumnFromTable(tableColumns);
        BiPredicate<Value, Value> whereFilter = where.generateWhereFilter(whereColumnFromTable);
        Value whereFilterValue = where.getWhereFilterValue(whereColumnFromTable);
        return schemaTablesContainer.getSchemaTables().get(tableName).getAllRowsWithFilterApplied(whereFilter, whereColumnFromTable, whereFilterValue);
    }

    private void executeTableConstraints(Where where, SchemaTable schemaTable, List<Column> tableColumns, List<DataRow> rowsToBeDeleted) {
        List<String> childTablesNames = schemaTable.getTableConstraints().getChildTablesNames();
        String onDelete = schemaTable.getTableConstraints().getOnDelete();
        if (childTablesNames != null) {
            deleteRowsFromChildTables(where, tableColumns, rowsToBeDeleted, childTablesNames, onDelete);
        }
    }


    private void deleteRowsFromChildTables(Where where, List<Column> tableColumns, List<DataRow> rowsToBeDeleted, List<String> childTablesNames, String onDelete) {
        childTablesNames.forEach(
                childTablesName -> {
                    SchemaTable childTable = schemaTablesContainer.getSchemaTables().get(childTablesName);
                    String fkColumn = childTable.getTableConstraints().getForeignKeyColumn();
                    DataRow columnFound = rowsToBeDeleted.stream().filter(dataRow ->
                            dataRow.getTableRowMap().get(fkColumn) != null
                    ).findFirst().orElse(null);
                    switch (onDelete) {
                        case "NO_ACTION":
                            throw new QueryExecutionException("can't delete because of the child tables have values related");
                        case "CASCADE":
                            Column whereColumnFromTable = where.getWhereColumnFromTable(tableColumns);
                            BiPredicate<Value, Value> whereFilterChild = where.generateWhereFilter(whereColumnFromTable);
                            Column childTableColumnByColumnName = childTable.getColumnByColumnName(childTable.getTableConstraints().getForeignKey());
                            List<DataRow> childTableRowsToBeDeleted = childTable.getAllRowsWithFilterApplied(whereFilterChild, childTableColumnByColumnName, columnFound.getValueObjectByKey(fkColumn));
                            System.out.println(childTableRowsToBeDeleted);
                            deleteRowsFromQuery(childTable, childTableRowsToBeDeleted);
                    }
                }
        );
    }


    private void deleteRowsFromQuery(SchemaTable schemaTable, List<DataRow> rowsToBeDeleted) {
        synchronized (schemaTablesContainer.getSchemaTables().get(schemaTable.getTableName())) {
            rowsToBeDeleted.forEach(row -> {
                Value<?> value = row.getValueObjectByKey("key");
                Integer key = (Integer) value.getValue();
                schemaTable.deleteRowFromTable(key);
            });
            fileManager.deleteListOfRows(schemaTable.getTableName(), rowsToBeDeleted);
        }
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setSchemaTablesContainer(SchemaTablesContainer schemaTablesContainer) {
        this.schemaTablesContainer = schemaTablesContainer;
    }
}
