package com.example.inmemoryweb.sqlcompiler.expression;

import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.UpdateContext;

import java.util.Map;

public class Update implements Expression {
    private final Map<String, String> valuesWithColumns;
    private final String table;
    private final Where where;

    public Update(Map<String, String> valuesWithColumns, String table, Where where) {
        this.valuesWithColumns = valuesWithColumns;
        this.table = table;
        this.where = where;
    }

    @Override
    public QueryResult interpret(Context ctx) {
        UpdateContext updateContext = (UpdateContext) ctx;
        updateContext.setSchemaTableName(table);
        updateContext.setValuesWithColumns(valuesWithColumns);
        updateContext.setWhere(where);
        return ctx.execute();

    }
}
