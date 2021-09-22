package com.example.inmemoryweb.databasestructure;

public class Column {
    private String columnName;
    private String datatype;

    public Column(String columnName, String datatype) {
        this.columnName = columnName;
        this.datatype = datatype;
    }

    public Column() {
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

}
