package com.example.inmemoryweb.sqlcompiler.parser.filter;

import com.example.inmemoryweb.databasestructure.Value;

import java.util.function.BiPredicate;

public interface Filter {
    BiPredicate<Value, Value> getFilter(String operation);
}
