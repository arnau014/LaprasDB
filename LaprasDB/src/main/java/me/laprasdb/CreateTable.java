package me.laprasdb;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Creates a new table
 */
public class CreateTable {

    private Map<String, Map<String, Object>> table;

    public CreateTable(String tablename, ArrayList<String> columns) {

    }

    // Placeholder method to create the file
    public void newTable(String[] name, Map<String, Map<String, Object>> table) throws IOException {
        /*File f = new File(name[0]);

        //if file does not exist, we create it
        if(!f.isFile()){
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(name[0]), "utf-8"))) {
            }
        }

        BufferedReader reader = new BufferedReader(new FileReader(name[0]));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();

        FileOutputStream fos =new FileOutputStream(f,true);
        Writer csvWriter = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 1; i < name.length; ++i ) {
            csvWriter.write(String.valueOf(lines++));
            csvWriter.write(',');
            csvWriter.write(name[i]);
            csvWriter.write("\n");
        }
        csvWriter.close();
    */
    }

    public Map<String, Map<String, Object>> getTable() {
        return table;
    }
}