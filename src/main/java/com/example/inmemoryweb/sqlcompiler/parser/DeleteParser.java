package com.example.inmemoryweb.sqlcompiler.parser;


import com.example.inmemoryweb.Exceptions.ParsingException;
import com.example.inmemoryweb.Utils.TokensChecker;
import com.example.inmemoryweb.sqlcompiler.expression.Delete;
import com.example.inmemoryweb.sqlcompiler.expression.Expression;
import com.example.inmemoryweb.sqlcompiler.expression.NullExpression;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("DeleteParser")
public class DeleteParser implements Parser {
    private TokensChecker tokensChecker;
    private WhereParser whereParser;

    @Autowired
    public void setTokensChecker(TokensChecker tokensChecker) {
        this.tokensChecker = tokensChecker;
    }

    @Autowired
    @Qualifier("WhereParser")
    public void setWhereParser(WhereParser whereParser) {
        this.whereParser = whereParser;
    }


    @Override
    public Expression parseQueryTokens(List<String> tokens) {
        if (tokens.size() < 3) {
            throw new ParsingException("Wrong formatted query only " + tokens.size() + " clauses found");
        }
        return getDeleteExpressionFromQueryTokens(tokens);
    }

    public Expression getDeleteExpressionFromQueryTokens(List<String> query) {
        int tokenIndex = 1;
        String tableName = null;
        String token = query.get(tokenIndex);
        if (tokensChecker.isTokenEqualFROM.test(token)) {
            tokenIndex++;
            tableName = query.get(tokenIndex);
        }
        return getExpression(query, tableName);
    }

    private Expression getExpression(List<String> query, String queryTableName) {
        if (queryTableName != null) {
            Where where = whereParser.handleWhereClause(query);
            return new Delete(queryTableName, where);
        }
        return new NullExpression();
    }

}
