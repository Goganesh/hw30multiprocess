package ru.otus.homework.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Change the port for Tomcat for the second server in application.properties
 */
@SpringBootApplication
public class FrontendStarter {

    public static void main(String[] args) {
        SpringApplication.run(FrontendStarter.class, args);
    }

}
