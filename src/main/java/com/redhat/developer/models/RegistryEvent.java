package com.redhat.developer.models;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class RegistryEvent {

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("eventId")
    private String eventId;

    public String getEventId() {
        return eventId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
