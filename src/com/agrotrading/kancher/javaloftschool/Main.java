package com.agrotrading.kancher.javaloftschool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        String strWord;
        List<String> tsStr = new ArrayList<>();
        Collection<String> syncCol = Collections.synchronizedCollection(tsStr);
        FileFindVisitor fileFindVisitor;
        Path folder;

        while (true) {

            strWord = userEnterCommand(StringApp.enterCommandAlert);

            if(checkExit(strWord, syncCol)) break;

            if((args = splitArgs(strWord)) == null) continue;

            if((folder = checkFolder(args[0], false)) == null) continue;

            syncCol.add(String.format(StringApp.resultRequestOutputHeader, strWord));

            fileFindVisitor = new FileFindVisitor(syncCol, Pattern.compile(args[1]));

            startTask(fileFindVisitor, folder);

            syncCol.add(StringApp.resultRequestOutputFooter);
        }
    }

    private static void startTask(FileFindVisitor fileFindVisitor, Path folder) {

        try {
            System.out.print(StringApp.waitListFiles);
            Files.walkFileTree(folder, fileFindVisitor);
            System.out.print(StringApp.done);
            System.out.print(StringApp.waitPool);
            fileFindVisitor.getResult();
            System.out.println(StringApp.done);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String userEnterCommand(String alert) {
        byte word [] = new byte[256];
        int counterChar;

        System.out.print(alert);

        try {
            counterChar = System.in.read(word);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

        return new String(word, 0, counterChar).trim();
    }

    private static String[] splitArgs(String strWord) {

        String[] args = strWord.split(" ", 2);
        if(args.length < 2) {
            System.out.printf(StringApp.errorFormat, strWord);
            return null;
        }
        return args;
    }

    private static Path checkFolder(String path, boolean write) {

        Path folder = Paths.get(path);

        if (!Files.exists(folder, LinkOption.NOFOLLOW_LINKS)) {
            System.out.println(StringApp.errorNullItem);
            return null;
        } else if(!Files.isDirectory(folder, LinkOption.NOFOLLOW_LINKS)) {
            System.out.println(StringApp.errorFileItem);
            return null;
        } else if(write && !Files.isWritable(folder)) {
            System.out.println(StringApp.errorFolderWrite);
            return null;
        }

        return folder;
    }

    private static boolean checkExit(String strWord, Collection<String> col) {

        if (StringApp.exitCommand.equalsIgnoreCase(strWord)) {
            if(col.size() > 0) {
                Path folder;
                Path savePath;
                BufferedWriter bufferedWriter;
                while(true) {
                    strWord = userEnterCommand(StringApp.enterPathSaveAlert);
                    if ((folder = checkFolder(strWord, true)) == null) continue;
                    savePath = Paths.get(folder.toString(), String.format(StringApp.templateOutputFile, new Date().getTime()));

                    try {
                        Files.createFile(savePath);
                        bufferedWriter = new BufferedWriter(new FileWriter(savePath.toFile()));
                        System.out.print(String.format(StringApp.waitSaveOutputFile, savePath.toString()));
                        for(String item : col) {
                            bufferedWriter.write(item);
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.close();
                        System.out.print(StringApp.done);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
            System.out.print(StringApp.goodbye);
            return true;
        }

        return false;
    }

}