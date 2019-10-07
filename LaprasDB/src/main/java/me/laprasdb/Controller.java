package me.laprasdb;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    private static final String template = "Created table %s";
    private final AtomicLong counter = new AtomicLong();

    /**
     * Creates a new table
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CreateTable create(@RequestBody PersonDTO perso) {
        return new CreateTable(counter.incrementAndGet(),);
    }


}