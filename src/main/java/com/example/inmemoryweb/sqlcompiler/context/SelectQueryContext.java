package com.example.inmemoryweb.sqlcompiler.context;

import com.example.inmemoryweb.Repository.SelectDataRepository;
import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Qualifier("SelectQueryContext")
public class SelectQueryContext implements SelectContext {
    private String schemaTableName;
    private List<String> queryColumns;
    private List<String> queryValues;
    private Map<String, String> valuesWithColumns;
    private Where where;

    private SelectDataRepository selectDataRepository;

    @Override
    public QueryResult execute() {
        return selectDataRepository.executeQuery(this);
    }

    @Autowired
    @Qualifier("SelectRepository")
    public void setSelectDataRepository(SelectDataRepository selectDataRepository) {
        this.selectDataRepository = selectDataRepository;
    }

    public String getSchemaTableName() {
        return schemaTableName;
    }

    public void setSchemaTableName(String schemaTableName) {
        this.schemaTableName = schemaTableName;
    }

    @Override
    public List<String> getQueryColumns() {
        return queryColumns;
    }

    public void setQueryColumns(List<String> queryColumns) {
        this.queryColumns = queryColumns;
    }

    public List<String> getQueryValues() {
        return queryValues;
    }

    public void setQueryValues(List<String> queryValues) {
        this.queryValues = queryValues;
    }

    public Map<String, String> getValuesWithColumns() {
        return valuesWithColumns;
    }

    public void setValuesWithColumns(Map<String, String> valuesWithColumns) {
        this.valuesWithColumns = valuesWithColumns;
    }

    @Override
    public Where getWhere() {
        return where;
    }

    @Override
    public void setWhere(Where where) {
        this.where = where;
    }
}
