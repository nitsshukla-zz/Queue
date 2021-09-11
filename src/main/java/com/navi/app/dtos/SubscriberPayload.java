package com.navi.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class SubscriberPayload {
    private String subscriberName;
    private long offset;
    private String payload;
}
