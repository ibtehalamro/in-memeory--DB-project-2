package com.example.inmemoryweb.sqlcompiler.context;

import com.example.inmemoryweb.Repository.InsertDataRepository;
import com.example.inmemoryweb.databasestructure.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("InsertQueryContext")
public class InsertQueryContext implements InsertContext {
    private String schemaTableName;
    private List<String> queryColumns;
    private List<String> queryValues;
    private InsertDataRepository insertDataRepository;

    @Autowired
    @Qualifier("InsertRepository")
    public void setInsertDataRepository(InsertDataRepository insertDataRepository) {
        this.insertDataRepository = insertDataRepository;
    }

    @Override
    public QueryResult execute() {
        return insertDataRepository.executeQuery(this);
    }

    @Override
    public String getSchemaTableName() {
        return schemaTableName;
    }

    @Override
    public void setSchemaTableName(String schemaTableName) {
        this.schemaTableName = schemaTableName;
    }

    @Override
    public List<String> getQueryColumns() {
        return queryColumns;
    }

    @Override
    public void setQueryColumns(List<String> queryColumns) {
        this.queryColumns = queryColumns;
    }

    @Override
    public List<String> getQueryValues() {
        return queryValues;
    }

    @Override
    public void setQueryValues(List<String> queryValues) {
        this.queryValues = queryValues;
    }


}
