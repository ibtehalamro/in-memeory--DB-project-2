package com.example.inmemoryweb.databasestructure;

import java.util.List;

public class QueryResult {
    private final List<DataRow> queryResult;
    private String tableName;
    private List<String> tableColumnsNames;

    public QueryResult(List<DataRow> queryResult, List<String> tableColumnsNames) {
        this.queryResult = queryResult;
        this.tableColumnsNames = tableColumnsNames;
    }

    public QueryResult(List<DataRow> queryResult) {
        this.queryResult = queryResult;
    }

    public List<DataRow> getQueryResult() {
        return queryResult;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getTableColumnsNames() {
        return tableColumnsNames;
    }

}
