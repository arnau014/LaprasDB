package me.laprasdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table {

    public Table(String tableName, ArrayList<String> columnNames) throws IOException {
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Create the table Map
        Map<String, Object> table = new HashMap<>();

        // Put the tableName into the table
        table.put("tableName", tableName);

        // Put the column names into the table
        table.put("columns", columnNames);

        // For each column, create a file which will have its data.
        // Each column is empty in the initial state
        new File("tables/" + tableName).mkdirs();
        for (String colName : columnNames) {
            objectMapper.writeValue(new File("tables/" + tableName + "/" + tableName + "_" + colName + ".json"),
                    new HashMap<>());
        }

        // Put the lastId to the table, which will be 0 for the first Id
        table.put("lastId", 0);

        //configure objectMapper for pretty input
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        //write Map object to json file
        objectMapper.writeValue(new File("tables/" + tableName + "/" + tableName + ".json"), table);
    }
}