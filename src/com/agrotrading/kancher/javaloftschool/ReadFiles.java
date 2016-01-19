package com.agrotrading.kancher.javaloftschool;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class ReadFiles implements Callable<Void> {

    Path path;
    Pattern linePattern;
    final Collection<String> col;

    public ReadFiles(Path path, Pattern linePattern, Collection<String> col) {
        this.path = path;
        this.linePattern = linePattern;
        this.col = col;
    }

    @Override
    public Void call() throws Exception {

        try {
            BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
            int count = 0;
            String line = br.readLine();

            while (line != null) {
                if(linePattern.matcher(line).matches()) {
                    count++;
                }
                line = br.readLine();
            }

            br.close();

            if(count > 0) {
                synchronized(col) {
                    col.add(String.format(StringApp.readFileResultFormat, path.getFileName(), count));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
