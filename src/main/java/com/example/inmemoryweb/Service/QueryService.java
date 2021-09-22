package com.example.inmemoryweb.Service;

import com.example.inmemoryweb.databasestructure.Query;
import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.QueryMessageExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class QueryService {
    private QueryMessageExecutor queryMessageExecutor;


    public QueryResult executeQuery(Query query) {
        String queryBody = query.getQueryBody();
        return queryMessageExecutor.executeMessage(queryBody);
    }

    @Autowired
    public void setQueryMessageExecutor(QueryMessageExecutor queryMessageExecutor) {
        this.queryMessageExecutor = queryMessageExecutor;
    }
}
