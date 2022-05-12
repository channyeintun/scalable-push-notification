package com.test.pushnotification.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryEmitterRepository implements EmitterRepository {

    private Map<String, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();

    @Override
    public void addOrReplaceEmitter(String uniqueToken, SseEmitter emitter) {
        userEmitterMap.put(uniqueToken, emitter);
    }

    @Override
    public void remove(String uniqueToken) {
        if (userEmitterMap != null && userEmitterMap.containsKey(uniqueToken)) {
            log.debug("Removing emitter for uniqueToken: {}", uniqueToken);
            userEmitterMap.remove(uniqueToken);
        } else {
            log.debug("No emitter to remove for uniqueToken: {}", uniqueToken);
        }
    }

    @Override
    public Optional<SseEmitter> get(String uniqueToken) {
        return Optional.ofNullable(userEmitterMap.get(uniqueToken));
    }
}
