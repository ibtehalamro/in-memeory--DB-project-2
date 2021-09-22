package com.example.inmemoryweb.databasestructure;


import com.example.inmemoryweb.Configuration.Constants;
import com.example.inmemoryweb.filemanager.FileManager;
import com.example.inmemoryweb.filemanager.TableFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@ApplicationScope
public class SchemaTablesContainer {
    private Hashtable<String, SchemaTable> schemaTables;

    private FileManager fileManager;
    private HttpSession httpSession;

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @PostConstruct
    public void initializeSchemaTables() {
        schemaTables = loadSchemaTablesInformation();
        generateTablesFiles();
        checkForForeignKeys();
        initializeTablesRelationShips();
        initializeTablesData();
    }
    private Hashtable<String, SchemaTable> loadSchemaTablesInformation() {
        File schemaTablesConfigFile = getSchemaTablesConfigFileObject();
        List<SchemaTable> tables = getSchemaTablesInformationAsList(schemaTablesConfigFile);
        httpSession.setAttribute("tables", tables);

        return convertSchemaTablesInformationToHashTable(tables);
    }

    private File getSchemaTablesConfigFileObject() {
        String tablesConfigFile = Constants.DB_TABLES_CONFIG_FILE_PATH;
        return new File(tablesConfigFile);
    }
    private List<SchemaTable> getSchemaTablesInformationAsList(File file) {
        List<SchemaTable> tables;
        tables = readSchemaTablesFromFile(file);
        return tables;
    }
    private List<SchemaTable> readSchemaTablesFromFile(File file) {
        List<SchemaTable> tables;
        Gson gson = new Gson();
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        tables = gson.fromJson(reader, new TypeToken<List<SchemaTable>>() {
        }.getType());
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tables;
    }

    private Hashtable<String, SchemaTable> convertSchemaTablesInformationToHashTable(List<SchemaTable> SchemaTablesInformationList) {
        Hashtable<String, SchemaTable> SchemaTablesInformation = new Hashtable<>();
        SchemaTablesInformationList.forEach(table -> SchemaTablesInformation.put(table.getTableName(), table));
        return SchemaTablesInformation;
    }

    private void generateTablesFiles() {
        schemaTables.forEach((tableName, schemaTable) -> {
            File file = fileManager.getJSONFile(tableName);
            TableFile tableFile = new TableFile(tableName, file);
            fileManager.addTableFile(tableName, tableFile);
        });
    }
    private void checkForForeignKeys() {
        schemaTables.forEach((s, schemaTable) -> {
            if (schemaTable.getTableConstraints() != null) {
                String foreignKey = schemaTable.getTableConstraints().getForeignKey();
                if (foreignKey != null) {
                    String foreignKeyColumn = schemaTable.getTableConstraints().getForeignKeyColumn();
                    String foreignKeyTable = schemaTable.getTableConstraints().getForeignKeyTable();
                    SchemaTable foreignKeySchemaTableObject = schemaTables.get(foreignKeyTable);
                    Column fkColumn = foreignKeySchemaTableObject.getColumnByColumnName(foreignKeyColumn);
                    Column newColumn = new Column();
                    newColumn.setDatatype(fkColumn.getDatatype());
                    newColumn.setColumnName(foreignKey);
                    schemaTable.getColumns().add(newColumn);
                }
            }
        });
    }
    private void initializeTablesRelationShips() {
        schemaTables.forEach((name, schemaTable) -> {
            schemaTables.forEach((table, loopTable) -> {
                if (!Objects.equals(table, name)) {
                    TableConstraints tableConstraints = loopTable.getTableConstraints();
                    if (tableConstraints != null && Objects.equals(tableConstraints.getForeignKeyTable(), name)) {
                        schemaTable.getTableConstraints().getChildTablesNames().add(table);
                    }
                }
            });
        });
    }

    private void initializeTablesData() {

        schemaTables.forEach((table, schemaTable) -> {
            loadTableData(table);
        });
    }

    private void loadTableData(String tableName) {
        Map<Integer, DataRow> fileData = fileManager.read(tableName);
        if (fileData.size() > 0)
            mapFileDataToTableData(tableName, fileData);
    }

    private void mapFileDataToTableData(String tableName, Map<Integer, DataRow> fileData) {
        SchemaTable table = schemaTables.get(tableName);

        fileData.forEach(table::insertRow);
    }

    public Hashtable<String, SchemaTable> getSchemaTables() {
        return schemaTables;
    }

}
