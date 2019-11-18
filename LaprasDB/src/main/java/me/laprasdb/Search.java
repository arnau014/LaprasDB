package me.laprasdb;

import javax.validation.constraints.NotBlank;

public class Search {
    private String tablename;
    private String id;
    private String match;

    public String getTablename() {
        return tablename;
    }

    public String getId() {
        return id;
    }

    public String getMatch() {
        return match;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMatch(String match) {
        this.match = match;
    }
}
