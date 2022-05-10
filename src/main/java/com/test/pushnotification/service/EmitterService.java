package com.test.pushnotification.service;

import com.test.pushnotification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class EmitterService {

    private Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String uniqueToken, SseEmitter emitter) {
        emitter.onCompletion(() -> emitters.remove(uniqueToken));
        emitter.onTimeout(() -> emitters.remove(uniqueToken));
        emitters.put(uniqueToken, emitter);
    }

    public void pushNotification(String uniqueToken, Notification notification) {
        log.info("pushing notification for token {}", uniqueToken);
        if (emitters.containsKey(uniqueToken)) {
            try {
                SseEmitter emitter = emitters.get(uniqueToken);
                if (emitter != null) {
                    SseEmitter.SseEventBuilder eventBuilder = SseEmitter
                            .event()
                            .name(uniqueToken)
                            .data(notification);
                    emitter.send(eventBuilder);
                }

            } catch (IOException | IllegalStateException e) {
                emitters.remove(uniqueToken);
            }
        }
    }
}
