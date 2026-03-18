package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;
    private Database database;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }

        database = new Database();

    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        command = command.trim();
        if (!command.trim().endsWith(";")) {
            return "[ERROR]: Missing semicolon";
        }
        try {
            if (command.toUpperCase().startsWith("USE")) {
                return handleUse(command);
            }
            if (command.toUpperCase().startsWith("CREATE")) {
                return handleCreate(command);
            }
            if (command.toUpperCase().startsWith("INSERT")) {
                return handleInsert(command);
            }
            if (command.toUpperCase().startsWith("SELECT")) {
                return handleSelect(command);
            }
            if (command.toUpperCase().startsWith("UPDATE")) {
                return handleUpdate(command);
            }
            if (command.toUpperCase().startsWith("DELETE")) {
                return handleDelete(command);
            }
            if (command.toUpperCase().startsWith("ALTER")) {
                return handleAlter(command);
            }
            if (command.toUpperCase().startsWith("DROP")) {
                return handleDrop(command);
            }
            if (command.toUpperCase().startsWith("JOIN")) {
                return handleJoin(command);
            }
            return "[ERROR]: Unknown command " + command;
        } catch (Exception e) {
            return "[ERROR]: " + e.getMessage();
        }

    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }

    private String currentDatabase;

    private final List<String> reservedKeywords = List.of(
            "USE","CREATE","DATABASE","TABLE","INSERT","INTO","VALUES",
            "SELECT","FROM","WHERE","UPDATE","SET","DELETE",
            "ALTER","ADD","DROP","JOIN","AND","OR","LIKE","TRUE","FALSE"
    );

    private boolean isReservedKeyword(String name) {
        return reservedKeywords.contains(name.toUpperCase());
    }

    private void saveDatabase() {

        try {

            File dbDir = new File(storageFolderPath + File.separator + currentDatabase);

            for(Table table : database.getTableValues()){
                File tableFile = new File(dbDir, table.getName() + ".tab");
                BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile));
                for(String col : table.getColumns()){
                    writer.write(col + "\t");
                }
                writer.newLine();
                for(Row row : table.getRows()){
                    for(String col : table.getColumns()){
                        writer.write(row.get(col) + "\t");
                    }
                    writer.newLine();
                }
                writer.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private String handleUse(String command) {

        command = command.replace(";","");
        String[] tokens = command.split("\\s+");
        if(tokens.length != 2) {
            return "[ERROR]: Invalid command format";
        }
        String dbName = tokens[1];
        File dbDir = new File(storageFolderPath + File.separator + dbName);
        if(!dbDir.exists()) {
            return "[ERROR]: Database does not exist";
        }
        currentDatabase = dbName;
        database = new Database();
        File[] files = dbDir.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.getName().endsWith(".tab")) {
                    String tableName = file.getName().replace(".tab", "");
                    try {
                        database.loadTable(storageFolderPath,dbName,tableName);
                    }catch(IOException e) {
                        return "[ERROR]: Could not load table " + tableName;
                    }
                }
            }
        }
        return "[OK]";
    }

    private String handleCreate(String command) {
        command = command.replace(";","");
        String[] tokens = command.split("\\s+");
        if(tokens[1].equalsIgnoreCase("DATABASE")){
            String dbName = tokens[2];
            if(isReservedKeyword(dbName)){
                return "[ERROR]: Database name is reserved";
            }
            File dbFolder = new File(storageFolderPath +File.separator + dbName);
            if(dbFolder.exists()){
                return "[ERROR]: Database already exists";
            }
            dbFolder.mkdir();
            return "[OK]";
        }
        if(tokens[1].equalsIgnoreCase("TABLE")){
            String tableName = tokens[2];
            if(isReservedKeyword(tableName)){
                return "[ERROR]: Table name is reserved";
            }
            if (currentDatabase == null) {
                return "[ERROR]: No database selected";
            }
            File tableFile = new File(storageFolderPath + File.separator + currentDatabase
                    + File.separator + tableName.toLowerCase()+ ".tab");
            if(tableFile.exists()){
                return "[ERROR]: Table already exists";
            }
            try{
                FileWriter writer = new FileWriter(tableFile);
                Table table = new Table(tableName.toLowerCase());
                writer.write("id");
                table.addColumn("id");
                int start = command.indexOf("(");
                int end = command.indexOf(")");
                if(start != -1 && end != -1){
                    String columnsPart = command.substring(start + 1, end);
                    String[] columns = columnsPart.split(",");
                    for(String col : columns){
                        String columnName = col.trim().toLowerCase();
                        if(isReservedKeyword(columnName)){
                            return "[ERROR]: Column name is reserved";
                        }
                        writer.write("\t" + columnName);
                        table.addColumn(columnName);
                    }
                }
                writer.write("\n");
                writer.close();
                database.addTable(table);
            } catch(IOException e){
                return "[ERROR]: Could not create table";
            }
            return "[OK]";
        }
        return "[ERROR]";
    }

    private String handleInsert(String command) {
        command = command.replace(";","");
        String[] parts = command.split("(?i)VALUES");
        String[] tokens = parts[0].trim().split("\\s+");
        String tableName = tokens[2];
        String valuesPart = parts[1].trim();
        valuesPart = valuesPart.substring(1, valuesPart.length() - 1);
        String[] values = valuesPart.split(",");
        for(int i = 0; i < values.length; i++){
            values[i] = values[i].trim();
            values[i] = values[i].replace("'","");
        }
        Table table = database.getTable(tableName);
        if(table == null){
            return "[ERROR]: No such table: " + tableName;
        }
        if(values.length != table.getColumns().size()-1){
            return "[ERROR]: Invalid number of value length: " + values.length;
        }
        Row row = new Row();
        int id = table.getNextId();
        row.set("id", String.valueOf(id));
        List<String>columns = table.getColumns();
        for(int i = 1; i < columns.size(); i++){
            row.set(columns.get(i), values[i-1]);
        }
        table.addRow(row);
        saveDatabase();
        return "[OK]";
    }

    private String handleSelect(String command) {
        command = command.replace(";","");
        String[] tokens = command.split("\\s+");
        String selectPart = command.substring(7, command.toUpperCase().indexOf("FROM")).trim();
        String tableName = tokens[3];
        Table table = database.getTable(tableName);
        if(table == null){
            return "[ERROR]: No such table: " + tableName;
        }
        List<String> selectedColumns = new ArrayList<>();
        if(selectPart.equals("*")){
            selectedColumns = table.getColumns();
        }else{
            String[] cols = selectPart.split(",");
            for(String c : cols){
                String colName = c.trim().toLowerCase();
                if(!table.getColumns().contains(colName)){
                    return "[ERROR]: Invalid column name: " + colName;
                }
                selectedColumns.add(colName);
            }
        }
        boolean hasWhere = command.toUpperCase().contains("WHERE");
        String condition = null;

        if(hasWhere){
            condition = command.substring(command.toUpperCase().indexOf("WHERE") + 5).trim();
        }
        StringBuilder result = new StringBuilder();
        for(String col : selectedColumns){
            result.append(col).append("\t");
        }
        result.append("\n");
        for(Row row : table.getRows()){
            if(condition != null && !evaluateCondition(row, condition)){
                continue;
            }
            for (String col : selectedColumns) {
                result.append(row.get(col)).append("\t");
            }
            result.append("\n");
        }
        return "[OK]\n" + result;
    }

    private boolean evaluateCondition(Row row, String condition) {
        condition = condition.replace("(", "").replace(")", "");
        if(condition.toUpperCase().contains("OR")){
            String[] parts = condition.split("(?i)OR");
            for(String part : parts){
                if(evaluateCondition(row, part.trim())){
                    return true;
                }
            }
            return false;
        }
        if(condition.toUpperCase().contains("AND")){
            String[] parts = condition.split("(?i)AND");
            for(String part : parts){
                if(!evaluateCondition(row, part.trim())){
                    return false;
                }
            }
            return true;
        }
        if(condition.toUpperCase().contains("LIKE")){
            String[] parts = condition.split("(?i)LIKE");
            String column = parts[0].trim();
            String value = parts[1].replace("'", "").trim();
            return row.get(column).contains(value);
        }
        if(condition.contains("==")){
            String[] parts = condition.split("==");
            String column = parts[0].trim();
            String value = parts[1].replace("'", "").trim();
            return row.get(column).equalsIgnoreCase(value);
        }
        if(condition.contains("!=")){
            String[] parts = condition.split("!=");
            String column = parts[0].trim();
            String value = parts[1].replace("'", "").trim();
            return !row.get(column).equalsIgnoreCase(value);
        }
        if(condition.contains(">=")){
            String[] parts = condition.split(">=");
            String column = parts[0].trim();
            String value = parts[1].replace("'", "").trim();
            return compareValues(row.get(column), value) >= 0;
        }
        if(condition.contains(">")){
            String[] parts = condition.split(">");
            String column = parts[0].trim();
            String value = parts[1].replace("'", "").trim();
            return compareValues(row.get(column), value) > 0;
        }
        if(condition.contains("<=")){
            String[] parts = condition.split("<=");
            String column = parts[0].trim();
            String value = parts[1].replace("'", "").trim();
            return compareValues(row.get(column), value) <= 0;
        }
        if(condition.contains("<")){
            String[] parts = condition.split("<");
            String column = parts[0].trim();
            String value = parts[1].replace("'", "").trim();
            return compareValues(row.get(column), value) < 0;
        }
        return false;
    }

    private int compareValues(String left, String right) {
        try {
            double l = Double.parseDouble(left);
            double r = Double.parseDouble(right);
            return Double.compare(l, r);
        }catch (Exception e){
            return left.compareTo(right);
        }
    }

    private  String handleUpdate(String command) {
        command = command.replace(";","");
        String[] parts = command.split("(?i)SET");
        String beforeSet = parts[0].trim();
        String afterSet = parts[1].trim();
        String tableName = beforeSet.split("\\s+")[1];
        String[] setWhere = afterSet.split("(?i)WHERE");
        String setPart = setWhere[0].trim();
        String condition = setWhere[1].trim();
        String[] setTokens = setPart.split("=");
        String setColumn = setTokens[0].trim();
        if(setColumn.equalsIgnoreCase("id")){
            return "[ERROR]: Cannot modify id column";
        }
        String setValue = setTokens[1].trim().replace("'","");
        Table table = database.getTable(tableName);
        if(table == null){
            return "[ERROR]: No such table: " + tableName;
        }
        if(!table.getColumns().contains(setColumn)){
            return "[ERROR]: Column does not exist: " + setColumn;
        }
        boolean updated = false;
        for(Row row : table.getRows()){
            if (evaluateCondition(row, condition)) {
                row.set(setColumn, setValue);
                updated = true;
            }
        }
        if(updated){
            saveDatabase();
        }
        return "[OK]";
    }

    private String handleDelete (String command) {
        command = command.replace(";","");
        String[] parts = command.split("(?i)WHERE");
        if(parts.length != 2){
            return "[ERROR]: Invalid command format";
        }
        String beforeWhere = parts[0].trim();
        String[] tokens = beforeWhere.split("\\s+");
        if(tokens.length < 3){
            return "[ERROR]: Invalid command format";
        }
        String tableName = tokens[2];
        String condition = parts[1].trim();
        Table table = database.getTable(tableName);
        if(table == null){
            return "[ERROR]: No such table: " + tableName;
        }
        boolean deleted = false;
        Iterator<Row> iterator = table.getRows().iterator();
        while(iterator.hasNext()){
            Row row = iterator.next();
            if (evaluateCondition(row, condition)) {
                iterator.remove();
                deleted = true;
            }
        }
        if(deleted){
            saveDatabase();
        }
        return "[OK]";
    }

    private String handleAlter(String command){
        command = command.replace(";","");
        String[] tokens = command.split("\\s+");
        if(tokens.length != 5){
            return "[ERROR]: Invalid command format";
        }
        String tableName = tokens[2];
        String operation = tokens[3].toUpperCase();
        String columnName = tokens[4].toLowerCase();
        if (isReservedKeyword(columnName)){
            return "[ERROR]: Reserved column name";
        }
        if(columnName.equalsIgnoreCase("id")){
            return "[ERROR]: Cannot modify id column";
        }
        Table table = database.getTable(tableName);
        if(table == null){
            return "[ERROR]: No such table: " + tableName;
        }
        if(operation.equals("DROP")){
            if(!table.getColumns().contains(columnName)){
                return "[ERROR]: Invalid column name: " + columnName;
            }
            table.getColumns().remove(columnName);
            for(Row row : table.getRows()){
                row.remove(columnName);
            }
        }else if(operation.equals("ADD")){
            if(table.getColumns().contains(columnName)){
                return "[ERROR]: Invalid column name: " + columnName;
            }
            table.getColumns().add(columnName);
            for(Row row : table.getRows()){
                row.set(columnName, "");
            }
        }else{
            return "[ERROR]: Invalid operation: " + operation;
        }
        saveDatabase();
        return "[OK]";
    }

    private String handleDrop(String command){
        command = command.replace(";","").trim();
        String[] tokens = command.split("\\s+");
        if(tokens.length < 3){
            return "[ERROR]: Invalid command format";
        }
        String type = tokens[1].toUpperCase();
        String name = tokens[2];
        if(type.equals("TABLE")) {
            if(currentDatabase == null){
                return "[ERROR]: No database selected";
            }
            String tableFilePath = storageFolderPath + File.separator + currentDatabase
                    + File.separator + name.toLowerCase() + ".tab";
            File tableFile = new File(tableFilePath);
            if(!tableFile.exists()){
                return "[ERROR]: Table not found: " + name;
            }
            if(tableFile.delete()){
                return "[OK]";
            }else{
                return "[ERROR]: Unable to delete table: " + name;
            }
        }else if(type.equals("DATABASE")){
            String dbPath = storageFolderPath + File.separator + name.toLowerCase();
            File dbDir =  new File(dbPath);
            if(!dbDir.exists()){
                return "[ERROR]: Database directory not found: " + name;
            }
            try{
                deleteDirectory(dbDir);
                if (currentDatabase != null && name.equalsIgnoreCase(currentDatabase)) {
                    currentDatabase = null;
                }
                return "[OK]";
            }catch(Exception e){
                return "[ERROR]: Unable to delete database: " + name;
            }
        }else{
            return "[ERROR]: Invalid operation: " + type;
        }
    }

    private void deleteDirectory(File dir)throws IOException{
        if(dir.isDirectory()){
            for(File file : dir.listFiles()){
                deleteDirectory(file);
            }
        }
        if(!dir.delete()){
            throw new IOException("Unable to delete directory: " + dir);
        }
    }

    private String handleJoin(String command){
        command = command.replace(";","");
        String[] tokens = command.split("\\s+");
        if(tokens.length < 8){
            return "[ERROR]: Invalid JOIN command format";
        }
        String table1Name = tokens[1];
        String table2Name = tokens[3];
        String column1 = tokens[5];
        String column2 = tokens[7];
        Table table1 = database.getTable(table1Name);
        Table table2 = database.getTable(table2Name);
        if(table1 == null || table2 == null){
            return "[ERROR]: Invalid command format";
        }
        boolean col1Exists = table1.getColumns().stream().anyMatch(c -> c.equalsIgnoreCase(column1));
        boolean col2Exists = table2.getColumns().stream().anyMatch(c -> c.equalsIgnoreCase(column2));
        if (!col1Exists || !col2Exists) {
            return "[ERROR]: Invalid attribute";
        }
        StringBuilder result = new StringBuilder();
        result.append("id").append("\t");
        for(String col : table1.getColumns()){
            if(!col.equalsIgnoreCase("id")){
                result.append(table1Name).append(".").append(col).append("\t");
            }
        }
        for(String col : table2.getColumns()){
            if(!col.equalsIgnoreCase("id")){
                result.append(table2Name).append(".").append(col).append("\t");
            }
        }
        result.append("\n");
        int newId = 1;
        for(Row row1 : table1.getRows()){
            String value1 = row1.get(column1);
            for(Row row2 : table2.getRows()){
                String value2 = row2.get(column2);
                if(value1 != null && value1.equals(value2)){
                    result.append(newId++).append("\t");
                    for(String col : table1.getColumns()){
                        if(!col.equalsIgnoreCase("id")){
                            result.append(row1.get(col)).append("\t");
                        }
                    }
                    for (String col : table2.getColumns()){
                        if(!col.equalsIgnoreCase("id")){
                            result.append(row2.get(col)).append("\t");
                        }
                    }
                    result.append("\n");
                }
            }
        }
        return "[OK]\n" + result;
    }
}