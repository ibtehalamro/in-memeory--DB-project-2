package com.example.inmemoryweb.sqlcompiler.parser;

import com.example.inmemoryweb.sqlcompiler.expression.Expression;
import com.example.inmemoryweb.sqlcompiler.expression.NullExpression;

import java.util.List;

public class NullParser implements Parser {

    @Override
    public Expression parseQueryTokens(List<String> tokens) {
        return new NullExpression();
    }
}
