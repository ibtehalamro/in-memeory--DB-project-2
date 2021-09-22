package com.example.inmemoryweb.sqlcompiler.expression;

import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;


public interface Expression {
    QueryResult interpret(Context ctx);
}
