package com.example.inmemoryweb.sqlcompiler.expression;

import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.SelectContext;

import java.util.List;

public class Select implements Expression {
    private final List<String> queryColumns;
    private final String table;
    private final Where where;

    public Select(List<String> queryColumns, String table, Where where) {
        this.queryColumns = queryColumns;
        this.table = table;
        this.where = where;
    }

    @Override
    public QueryResult interpret(Context ctx) {
        SelectContext selectContext = (SelectContext) ctx;
        selectContext.setSchemaTableName(table);
        selectContext.setWhere(where);
        selectContext.setQueryColumns(queryColumns);
        return ctx.execute();
    }
}
