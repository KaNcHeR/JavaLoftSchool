package com.agrotrading.kancher.javaloftschool;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
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
            System.out.println("Работает поток с файлом " + path.getFileName());
            BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
            String line = br.readLine();
            int count = 0;
            Matcher matcher;

            while (line != null) {
                matcher = linePattern.matcher(line);
                if(matcher.matches()) {
                    count++;
                }
                line = br.readLine();
            }

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
