package me.laprasdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONObject;

@RestController
public class Controller {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CreateTable create(@RequestBody Map<String, Object> table ) throws IOException {
        String tablename = table.get("tablename").toString();
        ArrayList<String> columns = (ArrayList<String>) table.get("columns");
        return new CreateTable(tablename, columns);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(path = "table/{tablename}", method = RequestMethod.GET)
    public @ResponseBody String showtable(@PathVariable String tablename) throws IOException {

        File dir = new File("tables");
        JSONObject result;
        //String prettyFormatted;
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

        return result.toJSONString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RequestMapping(path = "table/{tablename}/{columnname}", method = RequestMethod.GET)
    public @ResponseBody String showcolumn(@PathVariable String tablename,@PathVariable String columnname) throws IOException, ParseException {

        File dir = new File("tables");
        HashMap<String,HashMap<String,Object>> result;
        Object column;
        JSONObject col;
        //String prettyFormatted;
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
            result = new ObjectMapper().readValue(matches[0], HashMap.class);
        }

        try{
            column = result.get("columns").get(columnname);
        }catch(NullPointerException e){
            throw new MyResourceNotFoundException("Trying to fetch a non-exixsting column");
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String res = ow.writeValueAsString(column);
        return res;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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

}




