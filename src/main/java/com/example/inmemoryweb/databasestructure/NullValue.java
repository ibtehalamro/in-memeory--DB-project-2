package com.example.inmemoryweb.databasestructure;

public class NullValue implements Value<String> {
    String value;

    public NullValue() {
        this.value = "";
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public void setValue(String value) {
        this.value = "";
    }

    @Override
    public String toString() {
        return "";
    }
}

