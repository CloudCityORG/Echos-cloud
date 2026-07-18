package com.murk.serviceaudit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication
@EnableFeignClients
public class ServiceAuditApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAuditApplication.class, args);
    }

}
