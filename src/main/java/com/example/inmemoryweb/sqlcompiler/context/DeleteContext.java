package com.example.inmemoryweb.sqlcompiler.context;

import com.example.inmemoryweb.sqlcompiler.expression.Where;

public interface DeleteContext extends Context {
    String getSchemaTableName();

    void setTable(String table);

    Where getWhere();

    void setWhere(Where where);
}
