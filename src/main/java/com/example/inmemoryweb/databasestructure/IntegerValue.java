package com.example.inmemoryweb.databasestructure;

import java.io.Serializable;

public class IntegerValue implements Value<Integer>, Serializable {
    int value;

    public IntegerValue() {
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer integer) {
        value = integer;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
