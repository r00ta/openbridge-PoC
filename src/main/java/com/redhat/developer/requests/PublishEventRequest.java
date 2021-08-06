package com.redhat.developer.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.developer.models.RegistryEvent;

public class PublishEventRequest {
    @JsonProperty("type")
    private RegistryEvent eventType;

    @JsonProperty("event")
    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public RegistryEvent getEventType() {
        return eventType;
    }

    public void setEventType(RegistryEvent eventType) {
        this.eventType = eventType;
    }
}
