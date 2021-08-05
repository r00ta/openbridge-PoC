package com.redhat.developer.models.filters;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StringEquals extends Filter {
    public static final String FILTER_TYPE_NAME = "StringEquals";
    private final String type = FILTER_TYPE_NAME;

    @JsonProperty("value")
    private String value;

    @Override
    public String getConditionTemplate() {
        return String.format(FiltersConstants.TEMPLATE, getCondition());
    }

    private String getCondition(){
        return String.format("%s = \"%s\"", key, value);
    }
}
