package com.example.inmemoryweb.Repository;

import com.example.inmemoryweb.Exceptions.TableException;
import com.example.inmemoryweb.databasestructure.*;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.SelectContext;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Repository("SelectRepository")
public class SelectRepository implements SelectDataRepository {

    private SchemaTablesContainer schemaTablesContainer;

    @Autowired
    public void setSchemaTablesContainer(SchemaTablesContainer schemaTablesContainer) {
        this.schemaTablesContainer = schemaTablesContainer;
    }

    @Override
    public QueryResult executeQuery(Context context) {
        SelectContext selectContext = (SelectContext) context;
        QueryResult rows;
        Where where = selectContext.getWhere();
        String tableName = selectContext.getSchemaTableName();
        SchemaTable schemaTable = getSchemaTable(tableName);
        List<Column> tableColumns = schemaTable.getColumns();
        List<String> queryColumns = selectContext.getQueryColumns();
        List<DataRow> tableRows ;
        boolean isAllColumnsSelected = queryColumns.size() == 1 && queryColumns.get(0).equals("*");
        synchronized (getSchemaTable(tableName)) {
            if (where == null) {
                tableRows = selectWithoutWhere(tableName, queryColumns, isAllColumnsSelected);
            } else {
                tableRows = selectWithWhere(tableName, tableColumns, where, isAllColumnsSelected, queryColumns);
            }
        }

        List<String> stringColumnNames = getColumnNamesForTheViewTable(tableColumns, queryColumns, isAllColumnsSelected);

        rows = new QueryResult(tableRows, stringColumnNames);
        rows.setTableName(tableName);
        return rows;
    }
    private SchemaTable getSchemaTable(String tableName) {
        SchemaTable table = schemaTablesContainer.getSchemaTables().get(tableName);
        if (table == null) throw new TableException("table with " + tableName + " is not found");
        return table;
    }
    private List<DataRow> selectWithoutWhere(String tableName, List<String> queryColumns, boolean isAllColumnsSelected) {
        if (isAllColumnsSelected) {
            return getSchemaTable(tableName).getAllRows();
        } else {
            return getSchemaTable(tableName).getValuesFromColumns(queryColumns);
        }
    }

    private List<DataRow> selectWithWhere(String tableName, List<Column> tableColumns, Where where, boolean isAllColumnsSelected, List<String> queryColumns) {

        Column whereColumnFromTable = where.getWhereColumnFromTable(tableColumns);
        BiPredicate<Value, Value> whereFilter = where.generateWhereFilter(whereColumnFromTable);
        Value whereFilterValue = where.getWhereFilterValue(whereColumnFromTable);

        SchemaTable schemaTable = getSchemaTable(tableName);
        if (isAllColumnsSelected) {
            return schemaTable.getAllRowsWithFilterApplied(whereFilter, whereColumnFromTable, whereFilterValue);
        } else {
            return schemaTable.getValuesFromColumnsWithFilterApplied(queryColumns, whereFilter, whereColumnFromTable, whereFilterValue);
        }
    }


    private List<String> getColumnNamesForTheViewTable(List<Column> tableColumns, List<String> queryColumns, boolean isAllColumnsSelected) {
        List<String> stringColumnNames;
        if (isAllColumnsSelected) {
            stringColumnNames = tableColumns.stream().map(Column::getColumnName).collect(Collectors.toList());
        } else {
            stringColumnNames = new ArrayList<>(queryColumns);
        }
        return stringColumnNames;
    }



}
