package com.murk.servicevote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.murk.servicevote", "com.murk.commonlib.api"})
public class ServiceVoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceVoteApplication.class, args);
    }

}
