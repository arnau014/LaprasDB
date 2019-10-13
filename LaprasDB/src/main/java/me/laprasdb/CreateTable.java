package me.laprasdb;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a new table
 */
public class CreateTable {

    private Map<String, Object> table;

    public CreateTable(String tablename, ArrayList<String> columns) {
        this.table = new HashMap<String, Object>();

        try {
            newTable(tablename, columns,  table);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Placeholder method to create the file
    public void newTable(String name, ArrayList<String> columns, Map<String, Object> table) throws IOException {
        File f = new File(name);

        //if file does not exist, we create it a big file for the table which will contain all column names inside, later we create files for each column.
        if(!f.isFile()){
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(name), "utf-8"))) {

                FileOutputStream fos =new FileOutputStream(f,true);
                Writer columnWriter = new BufferedWriter(new OutputStreamWriter(fos));

                for (int i = 0; i < columns.size(); ++i ) {
                    try (Writer colWriter = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(columns.get(i)), "utf-8"))) {
                        columnWriter.write(String.valueOf(i));
                        columnWriter.write(',');
                        columnWriter.write(columns.get(i));
                        columnWriter.write("\n");
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //trigger error
        }
    }

    public Map<String, Object> getTable() {
        return table;
    }
}