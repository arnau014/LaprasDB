package me.laprasdb;

import java.util.Map;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    /**
     * Creates a new table
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CreateTable create(@RequestBody String table ) {
        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = jsonParser.parseMap(table);
        System.out.println(map);
        return new CreateTable(table);
    }


}




