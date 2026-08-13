package com.murk.commonlib.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-websocket")
public interface WebSocketFeignClient {

    @PostMapping("/ws/sendAll")
    void sendMessageToAll(@RequestBody String message);

    @PostMapping("/ws/sendToUser")
    void sendToUser(@RequestParam("userId") String userId, @RequestBody String message);
}
