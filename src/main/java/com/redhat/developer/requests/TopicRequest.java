package com.redhat.developer.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.developer.models.Topic;

public class TopicRequest {

    @JsonProperty("name")
    private String name;

    public Topic toEntity(){
        Topic topic = new Topic();
        topic.setName(name);

        return topic;
    }

}
