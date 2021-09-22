package com.example.inmemoryweb.databasestructure;

public interface Value<T> {
    T getValue();

    void setValue(T t);
}
