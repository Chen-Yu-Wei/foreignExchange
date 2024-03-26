package com.example.foreignexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ForeignExchangeApplication {

    // 程式的進入點
    public static void main(String[] args) {
        SpringApplication.run(ForeignExchangeApplication.class, args);
    }
}
