package com.example.inmemoryweb.sqlcompiler.parser;

import com.example.inmemoryweb.Configuration.Constants;
import com.example.inmemoryweb.Exceptions.ParsingException;
import com.example.inmemoryweb.Utils.TokensChecker;
import com.example.inmemoryweb.sqlcompiler.expression.Expression;
import com.example.inmemoryweb.sqlcompiler.expression.Insert;
import com.example.inmemoryweb.sqlcompiler.expression.NullExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component("InsertParser")
public class InsertParser implements Parser {
    Predicate<List<String>> isTokensNumberSufficient = tokens1 ->
            tokens1.size() >= Constants.INSERT_MINIMUM_TOKENS_COUNT;

    private TokensChecker tokensChecker;

    @Override
    public Expression parseQueryTokens(List<String> tokens) {
        if (isTokensNumberSufficient.test(tokens)) {
            return insertParser(tokens);
        } else {
            throw new ParsingException("Wrong formatted query only " + tokens.size() + " clauses found");
        }
    }

    private Expression insertParser(List<String> query) {
        String queryRequestedTable = "-null-";
        int currentTokenIndex = 1;
        List<String> queryColumns = new LinkedList<>();
        List<String> queryValues ;
        String token = query.get(currentTokenIndex);

        boolean isCurrentTokenEqualINTO = checkCurrentTokenIfEqualINTO(token);
        currentTokenIndex += 1;

        if (isCurrentTokenEqualINTO) {
            queryRequestedTable = query.get(currentTokenIndex);
            currentTokenIndex += 1;
        }
        token = query.get(currentTokenIndex);
        boolean isCurrentTokenEqualValues = checkCurrentTokenIfEqualVALUES(token);

        if (isCurrentTokenEqualValues) {
            queryColumns.add("*");
            currentTokenIndex += 1;
        } else {
            queryColumns = getQueryColumns(query, currentTokenIndex);
            currentTokenIndex += 1 + queryColumns.size();
        }

        queryValues = getQueryValues(query, currentTokenIndex);
        Map<String, String> valuesWithColumns = new HashMap<>();

        for (int i = 0; i < queryColumns.size(); i++) {
            valuesWithColumns.put(queryColumns.get(i), queryValues.get(i));
        }

        if (queryValues.size() > 0 && queryColumns.size() > 0) {
            return new Insert(queryRequestedTable, queryColumns, queryValues);
        }
        return new NullExpression();
    }

    private List<String> getQueryValues(List<String> query, int currentTokenIndex) {
        List<String> queryValues = new ArrayList<>();

        while (currentTokenIndex < query.size()) {
            queryValues.add(query.get(currentTokenIndex));
            currentTokenIndex++;
        }
        return queryValues;
    }

    private boolean checkCurrentTokenIfEqualVALUES(String token) {
        return tokensChecker.isTokenEqualVALUES.test(token);
    }

    private List<String> getQueryColumns(List<String> query, int currentTokenIndex) {

        List<String> columnList = new ArrayList<>();
        String token = query.get(currentTokenIndex);

        while (!checkCurrentTokenIfEqualVALUES(token)) {
            String columnName = query.get(currentTokenIndex);
            try {
                columnList.add(columnName);
            } catch (Exception e) {
                return null;
            }
            currentTokenIndex++;
            token = query.get(currentTokenIndex);
        }
        return columnList;
    }
    @Autowired
    public void setTokensChecker(TokensChecker tokensChecker) {
        this.tokensChecker = tokensChecker;
    }
    private boolean checkCurrentTokenIfEqualINTO(String token) {
        return tokensChecker.isTokenEqualINTO.test(token);
    }
}
