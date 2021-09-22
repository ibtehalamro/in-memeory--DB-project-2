package com.example.inmemoryweb.sqlcompiler.expression;

import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;

public class NullExpression implements Expression {
    @Override
    public QueryResult interpret(Context ctx) {
        return null;
    }
}
