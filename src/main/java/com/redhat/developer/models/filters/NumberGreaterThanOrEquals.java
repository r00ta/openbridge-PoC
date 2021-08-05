package com.redhat.developer.models.filters;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class NumberGreaterThanOrEquals extends Filter {
    public static final String FILTER_TYPE_NAME = "NumberGreaterThanOrEquals";
    private final String type = FILTER_TYPE_NAME;

    @JsonProperty("value")
    private Double value;

    @Override
    public String getConditionTemplate() {
        return String.format(FiltersConstants.TEMPLATE, getCondition());
    }

    private String getCondition(){
        return String.format("%s >= %f", key, value);
    }
}
