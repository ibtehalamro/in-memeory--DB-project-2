package com.example.inmemoryweb.databasestructure;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class DataRow implements Serializable {
    private final Map<String, Value> tableRow;

    public DataRow() {
        tableRow = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    public void mapValueDataToTableColumn(Value value, String column) {
        tableRow.put(column, value);
    }

    public Map<String, Value> getTableRowMap() {
        return tableRow;
    }

    public Value<?> getValueObjectByKey(String key) {
        return tableRow.get(key);
    }

    public DataRow getDataRowForAllColumns(List<Column> columnList, List<String> queryValues) {
        DataRow row = new DataRow();
        int valuesIterator = 0;
        for (Column column : columnList) {
            String columnName = column.getColumnName();
            if (valuesIterator < queryValues.size()) {
                ValueFactory valueFactoryInjected = new ValueFactory(queryValues.get(valuesIterator), column);
                valuesIterator++;
                Value valueObject = valueFactoryInjected.getObjectFromFactory();
                row.mapValueDataToTableColumn(valueObject, columnName);
            } else {
                row.mapValueDataToTableColumn(new NullValue(), columnName);
            }
        }
        return row;
    }

    public DataRow getDataRowForSpecificColumns(List<Column> columnList, List<String> queryColumns, List<String> queryValues) {
        DataRow row = new DataRow();
        for (Column column : columnList) {
            String columnName = column.getColumnName();
            if (queryColumns.contains(columnName)) {
                ValueFactory valueFactoryInjected = new ValueFactory(queryValues.get(queryColumns.indexOf(columnName)), column);
                Value valueObject = valueFactoryInjected.getObjectFromFactory();
                row.mapValueDataToTableColumn(valueObject, column.getColumnName());
            } else {
                row.mapValueDataToTableColumn(new NullValue(), column.getColumnName());
            }
        }
        return row;
    }
}
