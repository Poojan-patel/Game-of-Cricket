package com.tekion.intern;

import com.tekion.intern.dbconnector.MySqlConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class CricketGameApplication {

    public static void main(String ... args){
        SpringApplication.run(CricketGameApplication.class, args);
    }
}
