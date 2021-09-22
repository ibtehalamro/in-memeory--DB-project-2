package com.example.inmemoryweb.sqlcompiler.context;

import com.example.inmemoryweb.databasestructure.QueryResult;
import org.springframework.stereotype.Component;

@Component
public class NullContext implements Context {

    @Override
    public QueryResult execute() {
        return null;
    }
}
