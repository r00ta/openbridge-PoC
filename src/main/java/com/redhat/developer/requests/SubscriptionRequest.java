package com.redhat.developer.requests;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.developer.models.Subscription;

public class SubscriptionRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("endpoint")
    private String endpoint;

    public Subscription toEntity(){
        Subscription subscription = new Subscription();
        subscription.setName(name);
        subscription.setEndpoint(endpoint);

        return subscription;
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
