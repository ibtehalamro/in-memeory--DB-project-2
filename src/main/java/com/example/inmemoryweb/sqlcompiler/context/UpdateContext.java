package com.example.inmemoryweb.sqlcompiler.context;


import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.expression.Where;

import java.util.Map;

public interface UpdateContext extends Context {

    Where getWhere();

    void setWhere(Where where);

    QueryResult execute();


    String getSchemaTableName();


    void setSchemaTableName(String schemaTableName);


    Map<String, String> getValuesWithColumns();

    void setValuesWithColumns(Map<String, String> valuesWithColumns);

}
