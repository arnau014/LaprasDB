package me.laprasdb;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import org.json.JSONObject;


@RestController
public class Controller {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Map<String, Object> table ) throws IOException {
        String tableName = table.get("tableName").toString();
        ArrayList<String> columns = (ArrayList<String>) table.get("columns");

        // Check if file exists
        File tempFile = new File("tables/" + tableName + "/" + tableName + ".json");

        // If the file exists, return CONFLICT ERROR
        if(tempFile.exists()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        // If doesn't exist, create the new table
        new Table(tableName, columns);
        return new ResponseEntity("Table deteled.",HttpStatus.CREATED);
    }

    @RequestMapping(path = "table/{tableName}", method = RequestMethod.DELETE)
    public ResponseEntity deletetable(@PathVariable String tableName) throws IOException {

        JSONObject result;
        if (new File("tables/" + tableName).exists()) {
            File tempFile = new File("tables/" + tableName);
            boolean deleted  = deleteDirectory(tempFile);
            if (deleted){
                return new ResponseEntity(HttpStatus.OK);
            }
        }else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/select", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object select(@RequestBody Map<String, Object> select ) throws IOException, ParseException {
        String operation = select.get("operation").toString();
        String tableName = select.get("tableName").toString();
        String columnName = select.get("column").toString();

        // Check if table exists
        File tempFile = new File("tables/" + tableName + "/" + tableName + ".json");

        // If the file does not exists, return NOT FOUND ERROR
        if(!tempFile.exists()){
            System.out.println("not-EXISTS");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Table table = new Table(tableName);

        JSONArray jsonRules = new JSONArray(table.showColumn(tableName,columnName ).replace("\n","").replace(" ","").replace("\\",""));

        if(operation.equals("sum")){
            int sum = 0;
            for (int i = 0; i < jsonRules.length(); i++) {
                System.out.println();

                JSONObject jsonObject = jsonRules.getJSONObject(i);
                Iterator<String> keys = jsonObject.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    sum += Integer.parseInt(jsonObject.getString(key));
                }
            }
            return JSONObject.quote(new Gson().toJson("total:"+sum));
        }else if(operation.equals("avg")){
            int sum = 0;
            int j = 0;
            for (int i = 0; i < jsonRules.length(); i++) {
                JSONObject jsonObject = jsonRules.getJSONObject(i);
                Iterator<String> keys = jsonObject.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    sum += Integer.parseInt(jsonObject.getString(key));
                    ++j;
                }
            }
            return JSONObject.quote(new Gson().toJson("avg:"+(sum/j)));
        }else if(operation.equals("min")){
            int min = 0;
            for (int i = 0; i < jsonRules.length(); i++) {
                JSONObject jsonObject = jsonRules.getJSONObject(i);
                Iterator<String> keys = jsonObject.keys();

                min = Integer.parseInt(jsonObject.getString(keys.next()));

                while(keys.hasNext()) {
                    String key = keys.next();
                    if(Integer.parseInt(jsonObject.getString(key)) < min) min = Integer.parseInt(jsonObject.getString(key));
                }
            }
            return JSONObject.quote(new Gson().toJson("min:"+min));
        }else if(operation.equals("max")){
            int max = 0;
            for (int i = 0; i < jsonRules.length(); i++) {
                JSONObject jsonObject = jsonRules.getJSONObject(i);
                Iterator<String> keys = jsonObject.keys();

                max = Integer.parseInt(jsonObject.getString(keys.next()));

                while(keys.hasNext()) {
                    String key = keys.next();
                    if(Integer.parseInt(jsonObject.getString(key)) > max) max = Integer.parseInt(jsonObject.getString(key));
                }
            }
            return JSONObject.quote(new Gson().toJson("max:"+max));
        }else if(operation.equals("count")){
            int i = 0;
            for (int i = 0; i < jsonRules.length(); i++) {
                JSONObject jsonObject = jsonRules.getJSONObject(i);
                Iterator<String> keys = jsonObject.keys();

                while(keys.hasNext()) {
                    keys.next();
                    ++i;
                }
            }
            return JSONObject.quote("rows:"+i);
        }
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResponseEntity insert(@RequestBody Map<String, Object> insert ) throws IOException {
        String tableName = insert.get("tableName").toString();
        Map<String, Object> attrs = (Map<String, Object>) insert.get("attrs");

        // Check if table exists
        File tempFile = new File("tables/" + tableName + "/" + tableName + ".json");

        // If the file does not exists, return NOT FOUND ERROR
        if(!tempFile.exists()){
            System.out.println("not-EXISTS");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        // If exists, insert into the table
        Table table = new Table(tableName);
        Boolean inserted = table.insert(attrs);

        // Check if insertion went well
        if (inserted)
            return new ResponseEntity(HttpStatus.CREATED);
        else
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(path = "table/{tableName}", method = RequestMethod.GET)
    public @ResponseBody String showtable(@PathVariable String tableName) throws IOException {

        File dir = new File("tables/"+tableName);
        JSONObject result;
        //String prettyFormatted;
        File[] matches = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.equals(tableName+".json");
            }
        });

        if(matches.length==0){
            throw new MyResourceNotFoundException("Trying to fetch a non-exixsting table");
        }else{
            result = new ObjectMapper().readValue(matches[0], JSONObject.class);

        }

        return result.toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(path = "table/{tableName}/{columnname}", method = RequestMethod.GET)
    public @ResponseBody String showcolumn(@PathVariable String tableName,@PathVariable String columnname) throws IOException, ParseException {

        Table table = new Table(tableName);
        return table.showColumn(tableName, columnname);

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(method = RequestMethod.GET, value={"search/{tableName}/{id}/{match}","search/{tableName}/{id}"})
    public @ResponseBody ResponseEntity<String> search(Search search) throws IOException {

        File dir = new File("tables/"+search.getTableName());
        HashMap<String,HashMap<String,Object>> result;
        String response = "";
        File[] matches = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.equals(search.getTableName()+".json");
            }
        });

        if(matches.length==0){
            throw new MyResourceNotFoundException("Trying to fetch a non-exixsting table");
        }else{
            result = new ObjectMapper().readValue(matches[0], HashMap.class);
        }

        Iterator hmIterator = result.get("columns").entrySet().iterator();

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            HashMap<String,String> map = (HashMap<String, String>) mapElement.getValue();

            for(String k : map.keySet()) {
                if(Integer.parseInt(k) == search.getId()) {
                    if(search.getMatch() != null) {
                        if(map.get(k).equals(search.getMatch())) {
                            response += map.get(k)+"\n";
                        }
                    } else {
                        response += map.get(k)+"\n";
                    }

                }
            }
        }

        return new ResponseEntity<>(response,HttpStatus.OK);
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

    //Function to delete a directory
    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}




