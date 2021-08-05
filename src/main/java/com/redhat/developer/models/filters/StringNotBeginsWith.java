package com.redhat.developer.models.filters;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class StringNotBeginsWith extends Filter {
    public static final String FILTER_TYPE_NAME = "StringNotBeginsWith";
    private final String type = FILTER_TYPE_NAME;

    @JsonProperty("values")
    private List<String> values;

    @Override
    public String getConditionTemplate() {
        return String.format(FiltersConstants.TEMPLATE, getCondition());
    }

    private String getCondition(){
        List<String> conditions = new ArrayList<>();
        for (String value : values) {
            conditions.add(String.format("(starts with (%s, \"%s\"))", key, value));
        }
        return "not(" + String.join(" or ", conditions) + ")";
    }
}
