package com.utility.laravelgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//java -Dserver.port=8080 -jar laravelgenerator.jar 
@SpringBootApplication(scanBasePackages = {"com.utility.laravelgenerator"})
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
