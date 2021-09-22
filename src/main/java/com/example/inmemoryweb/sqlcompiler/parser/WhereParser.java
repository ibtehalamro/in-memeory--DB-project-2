package com.example.inmemoryweb.sqlcompiler.parser;

import com.example.inmemoryweb.Utils.TokensChecker;
import com.example.inmemoryweb.sqlcompiler.expression.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Component("WhereParser")
@RequestScope
public class WhereParser {

    private TokensChecker tokensChecker;

    public Where handleWhereClause(List<String> query) {
        Where where;
        String operation;
        String whereColumnToken;
        String whereValueToken;

        int whereTokenIndex = query.indexOf("WHERE");
        if (whereTokenIndex == -1) return null;
        int currentTokenIndex = whereTokenIndex + 1;


        if (tokensChecker.isTokenIndexLessThanTokensCount.test(currentTokenIndex + 2, query.size())) {
            whereColumnToken = query.get(currentTokenIndex);
            currentTokenIndex += 1;

            operation = query.get(currentTokenIndex);
            currentTokenIndex += 1;

            whereValueToken = query.get(currentTokenIndex);
            where = new Where(whereColumnToken, operation, whereValueToken);
            return where;
        }
        return null;
    }
    @Autowired
    public void setTokensChecker(TokensChecker tokensChecker) {
        this.tokensChecker = tokensChecker;
    }

}
