package com.example.inmemoryweb.sqlcompiler.parser.filter;


import com.example.inmemoryweb.databasestructure.StringValue;
import com.example.inmemoryweb.databasestructure.Value;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class StringFilter extends WhereFilter {
    private final BiFunction<String, String, Integer> testStringFORBiggerAndLesser = String::compareTo;

    @Override
    public BiPredicate<Value, Value> getFilter(String operation) {
        return getStringValueFilter(operation);
    }

    private BiPredicate<Value, Value> getStringValueFilter(String operation) {
        BiPredicate<Value, Value> filter = null;

        switch (operation) {
            case "=":
                filter = getEqualFilter();
                break;
            case ">":
                filter = getGreaterThanFilter();
                break;
            case "<":
                filter = getLessThanFilter();
                break;
        }
        return filter;
    }

    private BiPredicate<Value, Value> getEqualFilter() {
        BiPredicate<Value, Value> filter;
        filter = (x, y) -> x.getValue().equals(y.getValue());
        return filter;
    }

    private BiPredicate<Value, Value> getGreaterThanFilter() {
        BiPredicate<Value, Value> filter;
        filter = (x, y) -> {
            StringValue v = (StringValue) x;
            StringValue z = (StringValue) y;
            Integer result = testStringFORBiggerAndLesser.apply(v.getValue(), z.getValue());
            return result > 0;
        };
        return filter;
    }

    private BiPredicate<Value, Value> getLessThanFilter() {
        BiPredicate<Value, Value> filter;
        filter = (x, y) -> {
            StringValue v = (StringValue) x;
            StringValue z = (StringValue) y;
            Integer result = testStringFORBiggerAndLesser.apply(z.getValue(), v.getValue());

            return result > 0;
        };
        return filter;
    }


}
