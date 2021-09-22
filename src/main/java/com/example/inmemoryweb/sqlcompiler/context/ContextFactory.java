package com.example.inmemoryweb.sqlcompiler.context;


import com.example.inmemoryweb.Configuration.CrudCommands;
import com.example.inmemoryweb.databasestructure.Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("ContextFactory")
public class ContextFactory implements Factory<Context> {
    private Context updateQueryContext;
    private Context deleteQueryContext;
    private Context selectContext;
    private Context insertQueryContext;

    public Context getContext(CrudCommands crudCommands) {
        switch (crudCommands) {
            case NOT_VALID_CRUD:
                return new NullContext();
            case SELECT:
                return selectContext;
            case DELETE:
                return deleteQueryContext;
            case UPDATE:
                return updateQueryContext;
            case INSERT:
                return insertQueryContext;
        }
        return new NullContext();

    }


    @Override
    public Context getObjectFromFactory(CrudCommands crudCommandsEnum) {
        return getContext(crudCommandsEnum);
    }

    @Autowired
    @Qualifier("UpdateQueryContext")
    public void setUpdateQueryContext(Context updateQueryContext) {
        this.updateQueryContext = updateQueryContext;
    }

    @Autowired
    @Qualifier("DeleteQueryContext")
    public void setDeleteQueryContext(Context deleteQueryContext) {
        this.deleteQueryContext = deleteQueryContext;
    }

    @Autowired
    @Qualifier("SelectQueryContext")
    public void setSelectContext(Context selectContext) {
        this.selectContext = selectContext;
    }

    @Autowired
    @Qualifier("InsertQueryContext")
    public void setInsertQueryContext(Context insertQueryContext) {
        this.insertQueryContext = insertQueryContext;
    }
}
