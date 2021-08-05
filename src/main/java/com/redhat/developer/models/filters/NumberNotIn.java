package com.redhat.developer.models.filters;

import java.util.List;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class NumberNotIn extends Filter {
    public static final String FILTER_TYPE_NAME = "NumberNotIn";
    private final String type = FILTER_TYPE_NAME;

    @JsonProperty("values")
    private List<Double> values;

    @Override
    public String getConditionTemplate() {
        return String.format(FiltersConstants.TEMPLATE, getCondition());
    }

    private String getCondition(){
        return String.format("not(list contains(%s, %s))", values, key);
    }
}
