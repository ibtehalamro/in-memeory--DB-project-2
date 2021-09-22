package com.example.inmemoryweb.databasestructure;

import java.util.LinkedList;
import java.util.List;

public class TableConstraints {
    private String foreignKey;
    private String foreignKeyTable;
    private String foreignKeyColumn;
    private String onDelete;
    private String onUpdate;
    private List<String> childTablesNames = new LinkedList<>();

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getForeignKeyTable() {
        return foreignKeyTable;
    }

    public void setForeignKeyTable(String foreignKeyTable) {
        this.foreignKeyTable = foreignKeyTable;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public String getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(String onDelete) {
        this.onDelete = onDelete;
    }

    public String getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(String onUpdate) {
        this.onUpdate = onUpdate;
    }

    public List<String> getChildTablesNames() {
        return childTablesNames;
    }

    public void setChildTablesNames(List<String> childTablesNames) {
        this.childTablesNames = childTablesNames;
    }
}
