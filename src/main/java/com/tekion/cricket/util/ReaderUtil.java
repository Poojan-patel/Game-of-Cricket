package com.tekion.cricket.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ReaderUtil {
    private static Scanner sc = new Scanner(System.in);
    private static Path projectResourcePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "queries");

    public static String getStringFromAcceptableValues(List<String> acceptableValues){
        String input = "";
        while(true){
            input = sc.nextLine().toUpperCase();
            if(acceptableValues.contains(input)){
                return input;
            }
            System.out.println("Enter Value from:" + acceptableValues);
        }
    }

    public static String getNonEmptyString(){
        String input = "";
        while(true){
            input = sc.nextLine();
            if(input.isEmpty()){
                System.out.println("Enter Non-empty Value");
            }
            else
                break;
        }
        return input;
    }

    public static int getIntegerInputInRange(int lower){
        int input;
        while(true){
            try{
                input = Integer.parseInt(sc.nextLine());
                if(input < lower) {
                    System.out.println("Value should not be less than " + lower);
                }
                else {
                    break;
                }
            } catch(NumberFormatException nfe){
                System.out.println("Enter an integer");
            }
        }
        return input;
    }

    public static int getIntegerInputInRange(int lower, int upper){
        int input;
        while(true){
            try{
                input = Integer.parseInt(sc.nextLine());
                if(input < lower || input > upper){
                    System.out.println(String.format("Value should be between %d and %d", lower, upper));
                }
                else
                    break;
            } catch(NumberFormatException nfe){
                System.out.println("Enter an integer");
            }
        }
        return input;
    }

    public static String readSqlFromFile(String dirName, String fileName) {
        Path pathToFile = Paths.get(projectResourcePath.toString(), dirName, fileName + ".sql");
        String sql = null;
        try {
            sql = new String(Files.readAllBytes(pathToFile));
        } catch (IOException e) {
            sql = "";
        }
        return sql;
    }
}
