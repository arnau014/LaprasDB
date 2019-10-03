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
     * @param values
     * @return
     */
    @RequestMapping("/create")
    public CreateTable create(@RequestParam String[] values) {
        return new CreateTable(counter.incrementAndGet(), values);
    }


}