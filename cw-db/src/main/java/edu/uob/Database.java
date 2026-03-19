package edu.uob;

import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.*;

public class Database {
    private Map<String,Table> tables;
    public Database(){
        tables = new HashMap<>();
    }
    public void addTable(Table table){
        tables.put(table.getName(),table);
    }
    public Table getTable(String name){
        return tables.get(name.toLowerCase());
    }
    public Collection<Table> getTableValues(){
        return tables.values();
    }

    public void loadTable(String storageFolderPath, String databaseName, String tableName) throws IOException {

        String filePath = storageFolderPath
                + File.separator
                + databaseName.toLowerCase()
                + File.separator
                + tableName.toLowerCase()
                + ".tab";

        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("Table file not found");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));

        Table table = new Table(tableName);

        String headerLine = reader.readLine();
        if (headerLine == null) {
            reader.close();
            return;
        }

        String[] columnNames = headerLine.split("\t");

        for (String column : columnNames) {
            table.addColumn(column);
        }
        String line;
        int maxId = 0;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) continue;
            String[] values = line.split("\t");
            if (values.length != columnNames.length) {
                continue;
            }
            Row row = new Row();

            for (int i = 0; i < columnNames.length; i++) {
                row.set(columnNames[i], values[i]);
            }

            int id = Integer.parseInt(values[0]);
            if (id > maxId) {
                maxId = id;
            }

            table.addRow(row);
        }
        reader.close();

        table.setNextId(maxId + 1);
        addTable(table);
    }
}
