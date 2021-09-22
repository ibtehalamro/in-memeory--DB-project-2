package com.example.inmemoryweb.databasestructure;

import com.example.inmemoryweb.Exceptions.TableException;
import com.example.inmemoryweb.bplusalgorithm.BPlusTree;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

public class SchemaTable {

    private final transient BPlusTree<Integer, DataRow> tableData;
    private transient int nextKeyAvailable = 1;
    private String tableName;
    private List<Column> columns;
    private TableConstraints tableConstraints;

    public SchemaTable() {
        tableData = new BPlusTree<>(4);

    }

    public TableConstraints getTableConstraints() {
        if (tableConstraints == null) tableConstraints = new TableConstraints();
        return tableConstraints;
    }

    public void setTableConstraints(TableConstraints tableConstraints) {
        this.tableConstraints = tableConstraints;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Column getColumnByColumnName(String columnName) {
        Optional<Column> c = this.columns.stream().filter(column -> column.getColumnName().equals(columnName)).findFirst();
        return c.orElse(null);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public int keyGenerator() {
        int key = nextKeyAvailable;
        while (true) {
            if (tableData.search(key) == null) {
                nextKeyAvailable = key;
                break;
            }
            key++;
        }
        return key;
    }

    public synchronized void insertRow(int key, DataRow row) {
        if (key <= 0) {
            throw new TableException("Key value must be positive and greater than zero");
        }
        tableData.insert(key, row);
    }

    public List<DataRow> getAllRows() {
        return tableData.getTableRows();
    }

    public List<DataRow> getAllRowsWithFilterApplied(BiPredicate<Value, Value> whereFilter, Column whereColumn, Value whereValue) {
        return tableData.getTableRowsWithWhereCondition(whereFilter, whereColumn, whereValue);
    }

    public List<DataRow> getValuesFromColumns(List<String> columns) {
        return tableData.getSpecificColumnsRows(columns);
    }

    public List<DataRow> getValuesFromColumnsWithFilterApplied(List<String> columns, BiPredicate<Value, Value> whereFilter, Column whereColumn, Value whereValue) {
        return tableData.getListOfValueForColumns(columns, whereFilter, whereColumn, whereValue);
    }

    public synchronized void deleteRowFromTable(int key) {
        tableData.delete(key);
    }


}
