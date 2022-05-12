package com.test.pushnotification.contoller;


import com.test.pushnotification.model.Notification;
import com.test.pushnotification.payload.NotificationRequest;
import com.test.pushnotification.service.EmitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin("*")
public class NotificationController {

    @Autowired
    private EmitterService emitterService;

    @GetMapping("/subscription")
    public SseEmitter subscribe(@RequestParam("uniqueToken") String uniqueToken) {
        log.info("subscribing...");
        long tokenTimeOut = 24 * 60 * 60 * 1000l;
        SseEmitter sseEmitter = new SseEmitter(tokenTimeOut);
        emitterService.addEmitter(uniqueToken, sseEmitter);

        log.info("subscribed");
        return sseEmitter;
    }

    @PostMapping("/notification/{uniqueToken}")
    public ResponseEntity<?> send(@PathVariable String uniqueToken, @RequestBody NotificationRequest request) {
        // ...
        // you can save and retrieve uniqueToken of the user from DB, this is just demo
        Map<String, Object> payload = new HashMap<>();
        payload.put("myValue", request.getMessage());
        Notification notification = Notification
                .builder()
                .from(request.getFrom())
                .payload(payload)
                .build();
      emitterService.publish(uniqueToken,notification);
        return ResponseEntity.ok().body("message pushed to uniqueToken " + uniqueToken);
    }
}
