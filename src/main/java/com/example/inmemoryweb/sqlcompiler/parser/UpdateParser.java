package com.example.inmemoryweb.sqlcompiler.parser;

import com.example.inmemoryweb.Exceptions.ParsingException;
import com.example.inmemoryweb.Utils.TokensChecker;
import com.example.inmemoryweb.sqlcompiler.expression.Expression;
import com.example.inmemoryweb.sqlcompiler.expression.NullExpression;
import com.example.inmemoryweb.sqlcompiler.expression.Update;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("UpdateParser")
public class UpdateParser implements Parser {
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

        if (tokens.size() < 6) {
            throw new ParsingException("Wrong formatted query only " + tokens.size() + " clauses found");
        }
        return updateParser(tokens);
    }

    private Expression updateParser(List<String> query) {
        int currentTokenIndex = 1;

        Map<String, String> valuesWithColumns;

        String queryRequestedTable = query.get(currentTokenIndex);
        currentTokenIndex += 1;
        if (queryRequestedTable == null) {
            return new NullExpression();
        }

        valuesWithColumns = getDataRow(query, currentTokenIndex);

        if (valuesWithColumns == null) {
            return new NullExpression();
        }

        Where where = whereParser.handleWhereClause(query);

        return new Update(valuesWithColumns, queryRequestedTable, where);
    }

    private Map<String, String> getDataRow(List<String> query, int currentTokenIndex) {
        Map<String, String> valuesWithColumns = new HashMap<>();

        if (tokensChecker.isTokenEqualSET.test(query.get(currentTokenIndex))) {
            currentTokenIndex += 1;
            valuesWithColumns = createRowFromQuery(query, currentTokenIndex);
        }
        return valuesWithColumns;
    }

    private Map<String, String> createRowFromQuery(List<String> query, int tokenIndex) {
        Map<String, String> valuesWithColumns = new HashMap<>();
        while (tokensChecker.isTokenIndexLessThanTokensCount.test(tokenIndex, query.size()) && !tokensChecker.isTokenEqualWHERE.test(query.get(tokenIndex))) {
            String columnName = query.get(tokenIndex);
            tokenIndex += 2;
            String value = query.get(tokenIndex);
            tokenIndex++;

            valuesWithColumns.put(columnName, value);

        }
        return valuesWithColumns;
    }

}
