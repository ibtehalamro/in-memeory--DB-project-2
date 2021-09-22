package com.example.inmemoryweb.Repository;

import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;

public interface DataRepository {
    QueryResult executeQuery(Context context);
}
