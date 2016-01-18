package com.agrotrading.kancher.javaloftschool;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        String strWord;
        List<String> tsStr = new ArrayList<>();
        Collection<String> syncCol = Collections.synchronizedCollection(tsStr);
        Path folder;

        while (true) {

            strWord = userEnterCommand();

            if(checkExit(strWord, syncCol)) {
                break;
            }

            if((args = splitArgs(strWord)) == null) {
                continue;
            }

            if((folder = checkFolder(args[0])) == null) {
                continue;
            }

            try {
                syncCol.add(String.format(StringApp.resultRequestOutputHeader, strWord));
                FileFindVisitor fileFindVisitor = new FileFindVisitor(syncCol, Pattern.compile(args[1]));
                System.out.print(StringApp.waitPool);
                Files.walkFileTree(folder, fileFindVisitor);
                fileFindVisitor.getResult();
                syncCol.add(StringApp.resultRequestOutputFooter);
                System.out.println(StringApp.done);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private static String userEnterCommand() {
        byte word [] = new byte[256];
        int counterChar;

        System.out.print(StringApp.enterCommandAlert);

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

    private static Path checkFolder(String path) {

        Path folder = Paths.get(path);

        if (!Files.exists(folder, LinkOption.NOFOLLOW_LINKS)) {
            System.out.println(StringApp.errorNullItem);
            return null;
        } else if(!Files.isDirectory(folder, LinkOption.NOFOLLOW_LINKS)) {
            System.out.println(StringApp.errorFileItem);
            return null;
        }

        return folder;
    }

    private static boolean checkExit(String strWord, Collection<String> col) {

        if (StringApp.exitCommand.equalsIgnoreCase(strWord)) {
            for(String item : col) {
                System.out.println(item);
            }
            System.out.print(StringApp.goodbye);
            return true;
        }

        return false;
    }

}