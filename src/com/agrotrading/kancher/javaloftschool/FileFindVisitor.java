package com.agrotrading.kancher.javaloftschool;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class FileFindVisitor extends SimpleFileVisitor<Path> {

    Pattern linePattern;
    PathMatcher matcher;
    ExecutorService es = Executors.newCachedThreadPool();
    List<Callable<Void>> calls = new ArrayList<>();
    Collection<String> col;

    public FileFindVisitor(Collection<String> col, Pattern linePattern) {

        try {
            matcher = FileSystems.getDefault().getPathMatcher(StringApp.patternFileExtension);

        } catch (IllegalArgumentException iae) {
            System.err.println("Invalid pattern; did you forget to prefix \"glob:\" or \"regex:\"?");
            System.exit(1);
        }
        this.col = col;
        this.linePattern = linePattern;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        find(file);
        return super.visitFile(file, attrs);

    }

    private void find(Path path) {
        Path name = path.getFileName();
        if (matcher.matches(name)) {
            ReadFiles readFiles = new ReadFiles(path, linePattern, col);
            calls.add(readFiles);
            es.submit(readFiles);
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return super.preVisitDirectory(dir, attrs);
    }

    public void getResult() {

        try {
            es.invokeAll(calls);
            es.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
