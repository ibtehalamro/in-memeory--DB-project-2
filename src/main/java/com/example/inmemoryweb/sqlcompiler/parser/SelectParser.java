package com.example.inmemoryweb.sqlcompiler.parser;

import com.example.inmemoryweb.Exceptions.ParsingException;
import com.example.inmemoryweb.Utils.TokensChecker;
import com.example.inmemoryweb.sqlcompiler.expression.Expression;
import com.example.inmemoryweb.sqlcompiler.expression.NullExpression;
import com.example.inmemoryweb.sqlcompiler.expression.Select;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("SelectParser")
public class SelectParser implements Parser {
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

        if (tokens.size() < 4) {
            throw new ParsingException("Wrong formatted query only " + tokens.size() + " clauses found");
        }
        return selectParserHandler(tokens);
    }

    private Expression selectParserHandler(List<String> query) {

        int tokenIndex = 1;
        String queryTableName = null;
        List<String> queriedColumns;

        queriedColumns = getQueryColumns(query, tokenIndex);
        tokenIndex += queriedColumns.size();
        String token = query.get(tokenIndex);
        if (tokensChecker.isTokenEqualFROM.test(token)) {
            tokenIndex += 1;

            queryTableName = query.get(tokenIndex);
        }
        if (queryTableName != null) {
            Where where = whereParser.handleWhereClause(query);

            return new Select(queriedColumns, queryTableName, where);
        }
        return new NullExpression();
    }

    private List<String> getQueryColumns(List<String> query, int index) {
        List<String> queriedColumns = new ArrayList<>();

        if (tokensChecker.isTokenEqualStar.test(query.get(index))) {
            queriedColumns.add("*");
        } else {
            while (tokensChecker.isTokenIndexLessThanTokensCount.test(index, query.size()) && !tokensChecker.isTokenEqualFROM.test(query.get(index))) {
                queriedColumns.add(query.get(index));
                index++;
            }
        }
        return queriedColumns;
    }

}
