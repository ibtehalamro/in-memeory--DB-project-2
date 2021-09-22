package com.example.inmemoryweb.sqlcompiler.parser;


import com.example.inmemoryweb.sqlcompiler.expression.Expression;

import java.util.List;

public interface Parser {
    Expression parseQueryTokens(List<String> tokens);
}
