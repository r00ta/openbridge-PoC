package com.redhat.developer.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.developer.models.Filter;
import com.redhat.developer.models.FilterType;

public class FilterRequest {

    @JsonProperty("type")
    private FilterType filterType;

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private String value;

    public Filter toEntity(){
        Filter filter = new Filter();
        filter.setKey(key);
        filter.setType(filterType);
        filter.setValue(value);

        return filter;
    }

}
