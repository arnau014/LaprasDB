package me.laprasdb;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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


}




