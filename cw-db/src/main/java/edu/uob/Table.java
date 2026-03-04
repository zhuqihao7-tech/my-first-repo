package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private List<String> columns;
    private List<Row> rows;
    private int nextId;
    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.nextId = 1;
    }
    public String getName() {
        return name;
    }
    public void addColumn(String columnName) {
        columns.add(columnName.toLowerCase());
    }
    public List<String> getColumns() {
        return columns;
    }
    public List<Row> getRows() {
        return rows;
    }
    public void addRow(Row row) {
        rows.add(row);
    }
    public int getNextId() {
        return nextId++;
    }
    public void setNextId(int id) {
        this.nextId = id;
    }
}
