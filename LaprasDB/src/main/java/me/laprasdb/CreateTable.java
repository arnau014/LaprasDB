package me.laprasdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a new table
 */
public class CreateTable {

    private Map<String, Map<String, Object>> table;

    public CreateTable(String tableName, ArrayList<String> columnNames) throws IOException {
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Create the table Map
        Map<String, Object> table = new HashMap<>();

        // Put the tableName into the table
        table.put("tableName", tableName);

        // Create a new columns Map and fill it with the columnName(key) and an empty Map(value)
        Map<String, Map<Integer, Object>> columns = new HashMap<>();
        Map<Integer, Object> colInfo = new HashMap<>();

        for (String colName: columnNames){
            columns.put(colName, colInfo);
        }

        // Put the remaining fields to the table
        table.put("columns", columns);
        table.put("lastId", 0);

        //configure objectMapper for pretty input
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        //write Map object to json file
        objectMapper.writeValue(new File("tables/" + tableName + ".json"), table);

    }

    public Map<String, Map<String, Object>> getTable() {
        return table;
    }
}