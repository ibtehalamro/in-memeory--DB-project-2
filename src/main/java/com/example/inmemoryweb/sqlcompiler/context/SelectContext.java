package com.example.inmemoryweb.sqlcompiler.context;

import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.expression.Where;

import java.util.List;
import java.util.Map;

public interface SelectContext extends Context {

    Where getWhere();

    void setWhere(Where where);

    QueryResult execute();

    String getSchemaTableName();

    void setSchemaTableName(String schemaTableName);


    List<String> getQueryColumns();

    void setQueryColumns(List<String> queryColumns);

    List<String> getQueryValues();

    void setQueryValues(List<String> queryValues);

    Map<String, String> getValuesWithColumns();

    void setValuesWithColumns(Map<String, String> valuesWithColumns);


}
