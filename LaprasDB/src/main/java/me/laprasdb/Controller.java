package me.laprasdb;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controller {

    private static final String template = "Created table %s";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CreateTable create(@RequestBody Map<String, Object> table ) throws IOException {
        String tablename = table.get("tablename").toString();
        ArrayList<String> columns = (ArrayList<String>) table.get("columns");

        return new CreateTable(tablename, columns);
    }


}