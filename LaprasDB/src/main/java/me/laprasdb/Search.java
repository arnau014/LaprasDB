package me.laprasdb;

import javax.validation.constraints.NotBlank;

public class Search {
    private String tablename;
    private int id;
    private String match;

    public String getTableName() {
        return tablename;
    }

    public int getId() {
        return id;
    }

    public String getMatch() {
        return match;
    }

    public void setTableName(String tablename) {
        this.tablename = tablename;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMatch(String match) {
        this.match = match;
    }
}
