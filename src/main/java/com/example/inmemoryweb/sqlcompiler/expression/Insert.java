package com.example.inmemoryweb.sqlcompiler.expression;


import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.context.InsertContext;

import java.util.List;

public class Insert implements Expression {

    private final String schemaTableName;
    private final List<String> queryColumns;
    private final List<String> queryValues;


    public Insert(String schemaTableName, List<String> queryColumns, List<String> queryValues) {
        this.schemaTableName = schemaTableName;
        this.queryColumns = queryColumns;
        this.queryValues = queryValues;
    }

    @Override
    public QueryResult interpret(Context ctx) {
        InsertContext insertContext = (InsertContext) ctx;
        insertContext.setSchemaTableName(schemaTableName);
        insertContext.setQueryValues(queryValues);
        insertContext.setQueryColumns(queryColumns);
        return ctx.execute();
    }
}
