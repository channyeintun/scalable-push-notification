package com.test.pushnotification.model;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Notification {
    private String from;
    private Map<String,Object> payload;
}
