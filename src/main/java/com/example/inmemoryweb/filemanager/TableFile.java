package com.example.inmemoryweb.filemanager;

import java.io.File;

public final class TableFile {
    private final String tableName;
    private final File file;

    public TableFile(String tableName, File file) {
        this.tableName = tableName;
        this.file = file;
    }

    public String getTableName() {
        return tableName;
    }

    public File getFile() {
        return file;
    }
}
