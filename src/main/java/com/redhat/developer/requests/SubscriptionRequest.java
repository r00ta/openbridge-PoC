package com.redhat.developer.requests;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.developer.models.Subscription;

public class SubscriptionRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("transformationTemplate")
    private String transformationTemplate;

    @JsonProperty("filters")
    private List<FilterRequest> filters;

    public Subscription toEntity(){
        Subscription subscription = new Subscription();
        subscription.setName(name);
        subscription.setEndpoint(endpoint);
        subscription.setTransformationTemplate(transformationTemplate);
        subscription.setFilters(filters.stream().map(FilterRequest::toEntity).collect(Collectors.toList()));

        return subscription;
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
