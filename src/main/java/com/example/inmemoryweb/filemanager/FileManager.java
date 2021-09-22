package com.example.inmemoryweb.filemanager;

import com.example.inmemoryweb.Exceptions.FilesException;
import com.example.inmemoryweb.InterfaceAdapter;
import com.example.inmemoryweb.databasestructure.DataRow;
import com.example.inmemoryweb.databasestructure.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ApplicationScope
public class FileManager {

  private final   Map<String, TableFile> tableFiles = new ConcurrentHashMap<>();

    public void addTableFile(String fileName, TableFile tableFile) {
        tableFiles.put(fileName, tableFile);
    }

    public File getJSONFile(String filename) {
        File myFile = null;
        try {
         myFile=createNewFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myFile;
    }

    private File createNewFile(String filename) throws IOException {
        File myFile = new File("tables/" + filename + ".json");
        if (myFile.createNewFile()) {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
        }
        return myFile;
    }

    public void insertRowIntoTableFile(String tableName, int key, DataRow row) {
        Map<Integer, DataRow> data;
        data = read(tableName);
        synchronized (tableFiles.get(tableName)) {
            data.put(key, row);
            Gson gson = getGson();
            File file = tableFiles.get(tableName).getFile();
            writeToFile(data, gson, file);
        }
    }

    public void updateRowIntoTableFile(String tableName, int keyToPreviousRow, int key, DataRow row) {
        Map<Integer, DataRow> data;
        data = read(tableName);
        synchronized (tableFiles.get(tableName)) {
            data.remove(keyToPreviousRow);
            data.put(key, row);
            Gson gson = getGson();
            File file = tableFiles.get(tableName).getFile();
            writeToFile(data, gson, file);
        }
    }

    public void deleteListOfRows(String tableName, List<DataRow> rowsToBeDeleted) {
        Map<Integer, DataRow> tableData;
        tableData = read(tableName);
        synchronized (tableFiles.get(tableName)) {
            rowsToBeDeleted.forEach(row ->
                    tableData.remove(row.getValueObjectByKey("key").getValue()));
            Gson gson = getGson();
            File file = tableFiles.get(tableName).getFile();
            writeToFile(tableData, gson, file);
        }
    }

    private void writeToFile(Map<Integer, DataRow> tableData, Gson gson, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(tableData, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, DataRow> read(String tableName) {
        Map<Integer, DataRow> data;
        Gson gson = getGson();
        synchronized (tableFiles.get(tableName)) {
            File file = tableFiles.get(tableName).getFile();
            data = getDataRowMap(gson, file);
            if (data == null) return new HashMap<>();
        }
        return data;
    }

    private Map<Integer, DataRow> getDataRowMap(Gson gson, File file) {
        Map<Integer, DataRow> data;
        try (FileReader reader = new FileReader(file)) {
            data = gson.fromJson(reader, new TypeToken<Map<Integer, DataRow>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            throw new FilesException("File not found");
        } catch (IOException e) {
            throw new FilesException("error found with the required file");
        }
        return data;
    }

    private Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Value.class, new InterfaceAdapter());
        return builder.create();
    }

    @Override
    public String toString() {
        return "FileManager{" +
                "tableFiles=" + tableFiles +
                '}';
    }
}
