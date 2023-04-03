package org.example;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Properties;

public class readProperties {

    public static @NotNull Properties readProp(String propFileName) {

        Properties prop = new Properties();

        // https://dydals5678.tistory.com/108 참고
        try {
            FileReader resources = new FileReader(propFileName);
            try {
                prop.load(resources);
                System.out.println("qManager = " + prop.getProperty("qManager"));
                System.out.println("qName = " + prop.getProperty("qName"));
                System.out.println("fileRoute = " + prop.getProperty("fileRoute"));
                System.out.println("putFileName = " + prop.getProperty("putFileName"));
                System.out.println("getFileName = " + prop.getProperty("getFileName"));

            } catch (IOException e){
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return prop;

    }

}
