package com.murk.servicewebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceWebsocketApplication.class, args);
    }

}
