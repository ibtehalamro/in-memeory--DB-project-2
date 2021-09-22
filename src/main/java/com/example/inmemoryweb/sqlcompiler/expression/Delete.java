package com.example.inmemoryweb.sqlcompiler.expression;


import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.DeleteContext;

public class Delete implements Expression {
    private final String table;
    private final Where where;

    public Delete(String table, Where where) {
        this.table = table;
        this.where = where;
    }

    @Override
    public QueryResult interpret(Context ctx) {
        DeleteContext deleteContext = (DeleteContext) ctx;
        deleteContext.setTable(table);
        deleteContext.setWhere(where);
        return ctx.execute();
    }
}
