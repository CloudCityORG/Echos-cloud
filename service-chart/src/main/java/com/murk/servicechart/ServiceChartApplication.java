package com.murk.servicechart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceChartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceChartApplication.class, args);
    }

}
