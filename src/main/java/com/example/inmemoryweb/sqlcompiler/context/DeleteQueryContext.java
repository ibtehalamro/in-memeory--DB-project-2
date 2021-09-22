package com.example.inmemoryweb.sqlcompiler.context;

import com.example.inmemoryweb.Repository.DeleteDataRepository;
import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("DeleteQueryContext")
public class DeleteQueryContext implements DeleteContext {

    private String table;
    private Where where;
    private DeleteDataRepository deleteDataRepository;

    @Override
    public QueryResult execute() {
        return deleteDataRepository.executeQuery(this);
    }

    public String getSchemaTableName() {
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

    @Autowired
    @Qualifier("DeleteRepository")
    public void setDeleteDataRepository(DeleteDataRepository deleteDataRepository) {
        this.deleteDataRepository = deleteDataRepository;
    }
}
