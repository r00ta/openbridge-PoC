package com.redhat.developer.models;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@Embeddable
public class Filter {

    @JsonProperty("type")
    private FilterType type;

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private String value;

    public void setType(FilterType type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FilterType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
