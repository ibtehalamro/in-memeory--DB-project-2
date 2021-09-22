package com.example.inmemoryweb.databasestructure;

import java.io.Serializable;

public class StringValue implements Value<String>, Serializable {
    String value;

    public StringValue() {
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
