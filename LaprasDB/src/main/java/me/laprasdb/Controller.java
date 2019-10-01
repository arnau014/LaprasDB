package me.laprasdb;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private static final String template = "Created table %s";
    private final AtomicLong counter = new AtomicLong();

    /**
     * Creates a new table
     * @param tableName
     * @return
     */
    @RequestMapping("/create")
    public CreateTable create(@RequestParam(value="name", defaultValue="table1") String tableName) {
        return new CreateTable(counter.incrementAndGet(), tableName);
    }


}