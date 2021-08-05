package com.redhat.developer.requests;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.developer.models.Subscription;
import com.redhat.developer.models.filters.Filter;

public class SubscriptionRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("transformationTemplate")
    private String transformationTemplate;

    @JsonProperty("filters")
    private List<Filter> filters;

    public Subscription toEntity(){
        Subscription subscription = new Subscription();
        subscription.setName(name);
        subscription.setEndpoint(endpoint);
        subscription.setTransformationTemplate(transformationTemplate);
        // A little hack for nice api response....
        if (filters != null){
            subscription.setSerializedFilters(filters.stream().map(this::convertFilter).collect(Collectors.toSet()));
            subscription.setFiltersTemplates(filters.stream().map(Filter::getConditionTemplate).collect(Collectors.toSet()));
        }
        return subscription;
    }

    private String convertFilter(Filter filter){
        try {
            return new ObjectMapper().writeValueAsString(filter);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
