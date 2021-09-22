package com.example.inmemoryweb.sqlcompiler.context;

import java.util.List;


public interface InsertContext extends Context {

    String getSchemaTableName();
    void setSchemaTableName(String schemaTableName);
    List<String> getQueryColumns();
    void setQueryColumns(List<String> queryColumns);
    List<String> getQueryValues();
    void setQueryValues(List<String> queryValues);
}
