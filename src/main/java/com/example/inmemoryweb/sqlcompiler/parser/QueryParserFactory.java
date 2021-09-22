package com.example.inmemoryweb.sqlcompiler.parser;

import com.example.inmemoryweb.Configuration.CrudCommands;
import com.example.inmemoryweb.Exceptions.ParsingException;
import com.example.inmemoryweb.databasestructure.Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("QueryParserFactory")
public class QueryParserFactory implements Factory<Parser> {

    private Parser insertParser;

    private Parser selectParser;

    private Parser updateParser;

    private Parser deleteParser;



    Function<CrudCommands, Parser> getTokensParserInstance = crudCommandEnum -> {
        switch (crudCommandEnum) {
            case NOT_VALID_CRUD:
                throw new ParsingException("Command not found, please use the proper supported commands " +
                        "\t (UPDATE,INSERT,SELECT,DELETE)");
            case SELECT:
                return selectParser;
            case DELETE:
                return deleteParser;
            case UPDATE:
                return updateParser;
            case INSERT:
                return insertParser;
        }
        return new NullParser();
    };

    @Override
    public Parser getObjectFromFactory(CrudCommands crudCommandEnum) {
        return getTokensParserInstance.apply(crudCommandEnum);
    }
    @Autowired
    @Qualifier("InsertParser")
    public void setInsertParser(Parser insertParser) {
        this.insertParser = insertParser;
    }
    @Autowired
    @Qualifier("SelectParser")
    public void setSelectParser(Parser selectParser) {
        this.selectParser = selectParser;
    }
    @Autowired
    @Qualifier("UpdateParser")
    public void setUpdateParser(Parser updateParser) {
        this.updateParser = updateParser;
    }
    @Autowired
    @Qualifier("DeleteParser")
    public void setDeleteParser(Parser deleteParser) {
        this.deleteParser = deleteParser;
    }
}
