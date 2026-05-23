package br.com.fiap.clyvovet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ClyvoVetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClyvoVetApplication.class, args);
    }
}
