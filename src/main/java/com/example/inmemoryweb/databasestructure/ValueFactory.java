package com.example.inmemoryweb.databasestructure;

import com.example.inmemoryweb.Exceptions.ParsingException;

public class ValueFactory {
    String value;
    Column column;

    public ValueFactory(String value, Column column) {
        this.value = value;
        this.column = column;
    }

    private Value createValueObjectFromColumnType() {
        Value v = new NullValue();
        if (column == null) return v;
        if (column.getDatatype().equals("int")) {
            v = new IntegerValue();
            try {
                v.setValue(Integer.parseInt(value.trim()));
            } catch (NumberFormatException e) {
                throw new ParsingException("can not convert String value to int");
            }
        } else if (column.getDatatype().equals("string")) {
            v = new StringValue();
            v.setValue(value);
        }
        return v;
    }

    public Value getObjectFromFactory() {
        return createValueObjectFromColumnType();
    }
}
