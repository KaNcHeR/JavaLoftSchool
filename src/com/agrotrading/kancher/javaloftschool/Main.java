package com.agrotrading.kancher.javaloftschool;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        byte word [] = new byte[256];
        int counterChar;

        while (true) {

            System.out.print(StringApp.enterCommandAlert);

            try {
                counterChar = System.in.read(word);
            } catch (Exception ex) {
                System.out.println(ex.toString());
                break;
            }

            String strWord = new String(word, 0, counterChar).trim();

            if (StringApp.exitCommand.equalsIgnoreCase(strWord)) {
                System.out.print(StringApp.goodbye);
                break;
            } else {

                File myFolder = new File(strWord);

                File[] files = myFolder.listFiles();

                try {
                    for (File file : files) {
                        System.out.println(file.getName());
                    }
                } catch (NullPointerException e) {
                    System.out.println(StringApp.errorNullItem);
                }
            }
        }
    }
}