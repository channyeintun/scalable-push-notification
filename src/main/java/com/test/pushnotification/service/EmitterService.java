package com.test.pushnotification.service;

import com.test.pushnotification.model.Notification;
import com.test.pushnotification.repository.EmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
public class EmitterService {

    private final EmitterRepository repository;

    @Autowired
    public EmitterService(EmitterRepository repository) {
        this.repository = repository;
    }

    public void addEmitter(String uniqueToken, SseEmitter emitter) {
        emitter.onCompletion(() -> repository.remove(uniqueToken));
        emitter.onTimeout(() -> repository.remove(uniqueToken));
        repository.addOrReplaceEmitter(uniqueToken, emitter);
    }

    public void publish(String uniqueToken, Notification notification) {
        repository.get(uniqueToken).ifPresentOrElse(sseEmitter -> {
            try {
                log.debug("Sending event for token: {}", uniqueToken);
                sseEmitter.send(SseEmitter
                        .event()
                        .name(uniqueToken)
                        .data(notification));
            } catch (IOException | IllegalStateException e) {
                log.debug("Error while sending for member: {} - exception: {}", uniqueToken, e);
                repository.remove(uniqueToken);
            }
        }, () -> log.debug("No emitter for token {}", uniqueToken));
    }
}
