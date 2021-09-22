package com.example.inmemoryweb.sqlcompiler;

import com.example.inmemoryweb.Configuration.CrudCommands;
import com.example.inmemoryweb.databasestructure.Factory;
import com.example.inmemoryweb.databasestructure.QueryResult;
import com.example.inmemoryweb.sqlcompiler.context.Context;
import com.example.inmemoryweb.sqlcompiler.expression.Expression;
import com.example.inmemoryweb.sqlcompiler.parser.Parser;
import com.example.inmemoryweb.sqlcompiler.tokenizer.Tokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.function.Function;

@Component
@RequestScope
public class QueryMessageExecutor {
    private Tokenizer tokenizer;
    private Factory<Parser> parserFactory;
    private Factory<Context> contextFactory;

    private final Function<List<String>, String> getFirstToken = tokens -> {
        try {
            return tokens.get(0);
        } catch (IndexOutOfBoundsException e) {
            return " ";
        }
    };
    private final Function<String, CrudCommands> getCrudCommands = name -> {
        try {
            return CrudCommands.valueOf(name);
        } catch (IllegalArgumentException e) {
            return CrudCommands.NOT_VALID_CRUD;
        }
    };
    private final Function<String, List<String>> tokenizeQueryMessage = clientMessage ->
            tokenizer.getTokens(clientMessage);

    public QueryResult executeMessage(String queryMessage) {
        if (queryMessage.isEmpty()) return null;

        return getQueryResults(queryMessage);
    }

    private QueryResult getQueryResults(String queryMessage) {
        List<String> tokens = tokenizeQueryMessage.apply(queryMessage);

        String firstToken = getFirstToken.apply(tokens);
        CrudCommands crudCommandsEnum = getCrudCommands.apply(firstToken);

        Parser tokensParser = parserFactory.getObjectFromFactory(crudCommandsEnum);

        Expression queryExpression = tokensParser.parseQueryTokens(tokens);

        Context context = contextFactory.getObjectFromFactory(crudCommandsEnum);

        return queryExpression.interpret(context);
    }
    @Autowired
    @Qualifier("QueryTokenizer")
    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }
    @Autowired
    @Qualifier("QueryParserFactory")
    public void setParserFactory(Factory<Parser> parserFactory) {
        this.parserFactory = parserFactory;
    }
    @Autowired
    @Qualifier("ContextFactory")
    public void setContextFactory(Factory<Context> contextFactory) {
        this.contextFactory = contextFactory;
    }
}
