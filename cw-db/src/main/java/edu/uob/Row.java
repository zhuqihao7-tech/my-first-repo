package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private Map<String,String> data;
    public Row() {
        data = new HashMap<>();
    }
    public void set(String column, String value) {
        data.put(column.toLowerCase(),value);
    }
    public String get(String column) {
        return data.get(column.toLowerCase());
    }
    public Map<String,String> getAllData(){
        return data;
    }
    public void remove(String column){
        data.remove(column.toLowerCase());
    }
}
