package me.laprasdb;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONObject;

@RestController
public class Controller {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Map<String, Object> table ) throws IOException {
        String tablename = table.get("tablename").toString();
        ArrayList<String> columns = (ArrayList<String>) table.get("columns");

        // Check if file exists
        File tempFile = new File("tables/" + tablename + ".json");

        // If the file exists, return CONFLICT ERROR
        if(tempFile.exists()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        // If doesn't exist, create the new table
        new Table(tablename, columns);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(path = "table/{tablename}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> showtable(@PathVariable String tablename) throws IOException {

        File dir = new File("tables/"+tablename);
        JSONObject result;
        String result2 = "";
        File[] matches = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.equals(tablename+".json");
            }
        });

        if(matches.length==0){
            throw new MyResourceNotFoundException("Trying to fetch a non-exixsting table");
        }else{
            result = new ObjectMapper().readValue(matches[0], JSONObject.class);

        }

        ArrayList<String> columnas = (ArrayList<String>) result.get("columns");
        for(String c : columnas) {
            matches = dir.listFiles(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.equals(tablename+"_"+c+".json");
                }
            });
            if(matches.length==0){
                throw new MyResourceNotFoundException("Trying to fetch a non-exixsting column");
            }else{
                result2 += new ObjectMapper().readValue(matches[0], JSONObject.class)+"\n";

            }
        }

        return new ResponseEntity<String>(result2,HttpStatus.OK);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(method = RequestMethod.GET, value={"table/{tablename}/{id}","table/{tablename}/{id}/{match}"})
    public @ResponseBody ResponseEntity<String> showcolumn(Search search) throws IOException, ParseException {

        File dir = new File("tables/"+search.getTablename());
        HashMap<String,Object> column;
        String response = "";

        File[] matches = dir.listFiles( new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.equals(search.getTablename()+"_"+search.getId()+".json");
            }
        });
        if(matches.length==0){
            throw new MyResourceNotFoundException("Trying to fetch a non-exixsting column");
        }else{
            column = new ObjectMapper().readValue(matches[0], HashMap.class);
            if(search.getMatch() != null) {
                Iterator hmIterator = column.entrySet().iterator();
                while (hmIterator.hasNext()) {
                    Map.Entry mapElement = (Map.Entry)hmIterator.next();
                    if(mapElement.getValue().equals(search.getMatch())) {
                        response += mapElement.getValue().toString()+"\n";
                    }
                }

                return new ResponseEntity<String>(response,HttpStatus.OK);
            }
        }

        return new ResponseEntity<String>(column.toString(),HttpStatus.OK);
    }

    /*@JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(method = RequestMethod.GET, value={"search/{tablename}/{id}/{match}","search/{tablename}/{id}"})
    public @ResponseBody ResponseEntity<String> search(Search search) throws IOException {

        File dir = new File("tables");
        HashMap<String,HashMap<String,Object>> result;
        String response = "";
        File[] matches = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.equals(search.getTablename()+".json");
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
                if(k.equals(search.getId())) {
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
    }*/


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




