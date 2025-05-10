package com.DPhong.storeMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StoreMeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreMeApplication.class, args);
    }

}
