package me.laprasdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Table {

    private String tableName;

    public Table(String tableName) throws IOException {
        this.tableName = tableName;
    }

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

    public boolean insert(Map<String, Object> attrs) {
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //Check if all columns exist
        File tempFile;

        for ( String key : attrs.keySet() ) {
            tempFile = new File("tables/" + this.tableName + "/" + this.tableName + "_" + key + ".json");

            // If the file exists, return CONFLICT ERROR
            if(!tempFile.exists()){
                return false;
            }
        }

        //Get last id
        try {
            File tableFile = new File("tables/" + tableName + "/" + tableName + ".json");
            Map<String, Object> tableInfo =  objectMapper.readValue(tableFile, HashMap.class);
            int lastId = Integer.parseInt(tableInfo.get("lastId").toString());

            for ( String key : attrs.keySet() ) {
                HashMap<Integer,Object> column = objectMapper.readValue(new File("tables/" + this.tableName + "/" + this.tableName + "_" + key + ".json"), HashMap.class);
                // Write to each column the new value with the lastId as the key
                column.put(lastId, attrs.get(key));

                // Write again the column to the file
                objectMapper.writeValue(new File("tables/" + this.tableName + "/" + this.tableName + "_" + key + ".json"), column);
            }

            // Update the lastId
            tableInfo.put("lastId", lastId+1);
            objectMapper.writeValue(tableFile, tableInfo);
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}