package com.example.inmemoryweb.sqlcompiler.parser.filter;

import com.example.inmemoryweb.databasestructure.Value;

import java.util.function.BiPredicate;

public abstract class WhereFilter implements Filter {
    BiPredicate<Value, Value> whereFilter;
}
