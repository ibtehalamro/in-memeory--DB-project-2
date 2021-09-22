package com.example.inmemoryweb.sqlcompiler.context;


import com.example.inmemoryweb.Repository.UpdateDataRepository;
import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Qualifier("UpdateQueryContext")
public class UpdateQueryContext implements UpdateContext {
    private UpdateDataRepository updateDataRepository;

    private String table;

    private Map<String, String> valuesWithColumns;

    private Where where;

    @Override
    public QueryResult execute() {
        return updateDataRepository.executeQuery(this);
    }


    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Where getWhere() {
        return where;
    }

    public void setWhere(Where where) {
        this.where = where;
    }


    @Override
    public String getSchemaTableName() {
        return table;
    }

    @Override
    public void setSchemaTableName(String schemaTableName) {
        this.table = schemaTableName;
    }

    @Override
    public Map<String, String> getValuesWithColumns() {
        return valuesWithColumns;
    }

    @Override
    public void setValuesWithColumns(Map<String, String> valuesWithColumns) {
        this.valuesWithColumns = valuesWithColumns;
    }


    @Autowired
    @Qualifier("UpdateRepository")
    public void setUpdateDataRepository(UpdateDataRepository updateDataRepository) {
        this.updateDataRepository = updateDataRepository;
    }
}
