package com.tekion.intern;

import com.tekion.intern.controller.MatchController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class CricketGameApplication {

    public static void main(String ... args){
        SpringApplication.run(CricketGameApplication.class, args);
    }
}
