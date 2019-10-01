package me.laprasdb;

import java.util.*;

/**
 * Creates a new table
 */
public class CreateTable {

    private final long id;
    private final String content;
    // The table is a map with the column names as keys and the values are ArrayLists of the content
    private Map<String, ArrayList<Object>> table;

    public CreateTable(long id, String content) {
        this.id = id;
        this.content = content;
        this.table = new HashMap();

        newTable(content, table);
    }

    // Placeholder method to create the file
    public void newTable(String name, Map<String, ArrayList<Object>> table){

    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Map<String, ArrayList<Object>> getTable() {
        return table;
    }
}