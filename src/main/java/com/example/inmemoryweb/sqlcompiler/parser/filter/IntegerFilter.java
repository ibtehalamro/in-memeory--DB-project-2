package com.example.inmemoryweb.sqlcompiler.parser.filter;


import com.example.inmemoryweb.databasestructure.IntegerValue;
import com.example.inmemoryweb.databasestructure.Value;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class IntegerFilter extends WhereFilter {
    private final BiFunction<Integer, Integer, Integer> testIntFORBiggerAndLesser = (x, y) -> {
        if (x > y) {
            return 1;
        }
        return -1;
    };

    @Override
    public BiPredicate<Value, Value> getFilter(String operation) {
        return getIntegerValueFilter(operation);
    }

    private BiPredicate<Value, Value> getIntegerValueFilter(String operation) {
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

    BiPredicate<Value, Value> getEqualFilter() {
        BiPredicate<Value, Value> filter;
        filter = (x, y) -> x.getValue().equals(y.getValue());
        return filter;
    }

    private BiPredicate<Value, Value> getGreaterThanFilter() {
        BiPredicate<Value, Value> filter;
        filter = (x, y) -> {
            IntegerValue v = (IntegerValue) x;
            IntegerValue z = (IntegerValue) y;

            Integer result = testIntFORBiggerAndLesser.apply(v.getValue(), z.getValue());
            return result.equals(1);
        };
        return filter;
    }

    private BiPredicate<Value, Value> getLessThanFilter() {
        BiPredicate<Value, Value> filter;
        filter = (x, y) -> {
            IntegerValue v = (IntegerValue) x;
            IntegerValue z = (IntegerValue) y;

            Integer result = testIntFORBiggerAndLesser.apply(z.getValue(), v.getValue());
            return result.equals(1);
        };
        return filter;
    }

}
