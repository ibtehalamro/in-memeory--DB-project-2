package com.example.inmemoryweb.sqlcompiler.expression;

import com.example.inmemoryweb.databasestructure.Column;
import com.example.inmemoryweb.databasestructure.Value;
import com.example.inmemoryweb.databasestructure.ValueFactory;
import com.example.inmemoryweb.sqlcompiler.parser.filter.IntegerFilter;
import com.example.inmemoryweb.sqlcompiler.parser.filter.StringFilter;

import java.util.List;
import java.util.function.BiPredicate;

public class Where {

    private final String whereColumnToken;
    private final String operation;
    private final String whereValueToken;


    public Where(String whereColumnToken, String operation, String whereValueToken) {
        this.whereColumnToken = whereColumnToken;
        this.operation = operation;
        this.whereValueToken = whereValueToken;
    }

    public String getWhereColumnToken() {
        return whereColumnToken;
    }

    public String getWhereValueToken() {
        return whereValueToken;
    }

    public BiPredicate<Value, Value> generateWhereFilter(Column whereColumnFromTable) {
        BiPredicate<Value, Value> whereFilter = null;
        String columnType = whereColumnFromTable.getDatatype();
        if (columnType.equals("int")) {
            whereFilter = new IntegerFilter().getFilter(operation);
        } else if (columnType.equals("string")) {
            whereFilter = new StringFilter().getFilter(operation);
        }
        return whereFilter;
    }

    public Column getWhereColumnFromTable(List<Column> tableColumns) {
        return tableColumns.stream().filter(tableColumn -> tableColumn.getColumnName().equals(whereColumnToken)).findFirst().orElse(null);
    }

    public Value<?> getWhereFilterValue(Column whereColumnFromTable) {
        ValueFactory valueFactory = new ValueFactory(whereValueToken, whereColumnFromTable);
        return valueFactory.getObjectFromFactory();
    }
}
