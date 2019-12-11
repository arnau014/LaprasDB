package me.laprasdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.*;
import java.util.*;

public class Table {

    final int MAX_PAGE = 10;

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
            objectMapper.writeValue(new File("tables/" + tableName + "/" + tableName + "_" + colName + "_0" + ".json"),
                    new HashMap<>());
        }

        // Put the lastId to the table, which will be 0 for the first Id
        table.put("lastId", 0);

        // Put the lastPage to the table, which will be 0 initially
        table.put("lastPage", 0);

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

        //Get last id and last page
        try {
            File tableFile = new File("tables/" + tableName + "/" + tableName + ".json");
            Map<String, Object> tableInfo =  objectMapper.readValue(tableFile, HashMap.class);
            int lastId = Integer.parseInt(tableInfo.get("lastId").toString());
            int lastPage = Integer.parseInt(tableInfo.get("lastPage").toString());

            for ( String key : attrs.keySet() ) {
                tempFile = new File("tables/" + this.tableName + "/" + this.tableName + "_" + key + "_" + lastPage + ".json");

                // If the file exists, return CONFLICT ERROR
                if(!tempFile.exists()){
                    return false;
                }
            }

            // If the id get at the max number of elements in a page, we create new pages
            if(lastId % MAX_PAGE == 0 && lastId > 0) {
                lastPage++;
                new File("tables/" + tableName).mkdirs();
                for (String key : attrs.keySet()) {
                    objectMapper.writeValue(new File("tables/" + tableName + "/" + tableName + "_" + key + "_" + lastPage + ".json"),
                            new HashMap<>());
                }
                // And update the value of lastPage
                tableInfo.put("lastPage", lastPage);
                objectMapper.writeValue(tableFile, tableInfo);
            }

            // And then we can write the values in the such page
            for ( String key : attrs.keySet() ) {
                HashMap<Integer,Object> column = objectMapper.readValue(new File("tables/" + this.tableName + "/" + this.tableName + "_" + key + "_" + lastPage + ".json"), HashMap.class);
                // Write to each column the new value with the lastId as the key
                column.put(lastId, attrs.get(key));

                // Write again the column to the file
                objectMapper.writeValue(new File("tables/" + this.tableName + "/" + this.tableName + "_" + key + "_" + lastPage + ".json"), column);
            }

            // Update the lastId
            tableInfo.put("lastId", lastId+1);
            objectMapper.writeValue(tableFile, tableInfo);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public String showColumn(String tableName, String columnname) throws IOException {

        File dir = new File("tables/"+tableName);
        HashMap<String,HashMap<String,Object>> result;
        Object column;
        JSONObject col;
        String target_file ;
        List<String> fList = new ArrayList<String>();
        File[] folderToScan = dir.listFiles();
        String res="[";

        for (int i = 0; i < folderToScan.length; i++) {
            if (folderToScan[i].isFile()) {
                target_file = folderToScan[i].getName();
                if (target_file.startsWith(tableName+"_"+columnname)
                        && target_file.endsWith(".json")) {

                    fList.add(target_file);
                }
            }
        }


        if(fList.size()==0){
            throw new Table.MyResourceNotFoundException("Trying to fetch a non-exixsting column");
        }else{
            for (int i = 0; i < fList.size(); i++){
                String nm = fList.get(i);
                File[] matches = dir.listFiles(new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.equals(nm);
                    }
                });
                result = new ObjectMapper().readValue(matches[0], HashMap.class);
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                res += ow.writeValueAsString(result)+",";
            }
        }
        res += "]";
        return res;


    }

    //Error handling functions
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class MyResourceNotFoundException extends RuntimeException {
        public MyResourceNotFoundException() {
            super();
        }
        public MyResourceNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
        public MyResourceNotFoundException(String message) {
            super(message);
        }
        public MyResourceNotFoundException(Throwable cause) {
            super(cause);
        }
    }

}