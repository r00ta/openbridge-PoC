package com.redhat.developer.models.filters;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class BoolEquals extends Filter {
    public static final String FILTER_TYPE_NAME = "BoolEquals";

    @JsonProperty("value")
    private boolean value;

    @Override
    public String getConditionTemplate() {
        return String.format(FiltersConstants.TEMPLATE, getCondition());
    }

    private String getCondition(){
        return String.format("%s = \"%s\"", key, value);
    }
}
