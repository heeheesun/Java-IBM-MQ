package org.example;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Properties;

public class readProperties {

    public static @NotNull Properties readProp() {

        Properties prop = new Properties();
        String path = System.getProperty("user.dir");
        System.out.println("현재 작업 경로: " + path);
        // IntelliJ 설정으로 properties파일의 한글이 깨지면
        // Preferences - Editor - File Encoding - Default encoding for properties files : URF-8로 설정
        // 이미 깨진 한글은 다시 돌아오지 않기 때문에 값을 다시 넣어줘야함
        String propFileRoute = "\\src\\main\\resources\\test.properties";


        // https://dydals5678.tistory.com/108 참고
        try {
            FileReader resources = new FileReader(path + propFileRoute);
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
