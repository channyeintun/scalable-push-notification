package com.test.pushnotification.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.pushnotification.model.Notification;
import com.test.pushnotification.payload.RedisNotificationPayload;
import com.test.pushnotification.service.EmitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    @Autowired private EmitterService emitterService;

    ObjectMapper objectMapper = new ObjectMapper();
    public void onMessage(final Message message, final byte[] pattern) {
        try {
            var notificationPayload = objectMapper.readValue(message.toString(), RedisNotificationPayload.class);
            Map<String,Object> payload=new HashMap<>();
            payload.put("myValue",notificationPayload.getMessage());
            Notification notification = Notification
                    .builder()
                    .from(notificationPayload.getFrom())
                    .payload(payload)
                    .build();
            emitterService.pushNotification(notificationPayload.getUniqueToken(),notification);

        } catch (JsonProcessingException e) {
            log.error("unable to deserialize message ", e);
        }
    }
}
