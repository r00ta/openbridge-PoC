package com.redhat.developer.models.filters;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class NumberNotInRange extends Filter {
    public static final String FILTER_TYPE_NAME = "NumberNotInRange";
    private final String type = FILTER_TYPE_NAME;

    @JsonProperty("values")
    private List<List<Double>> values;

    @Override
    public String getConditionTemplate() {
        return String.format(FiltersConstants.TEMPLATE, getCondition());
    }

    private String getCondition(){
        List<String> conditions = new ArrayList<>();
        for (List<Double> condition : values){
            if (condition.size() != 2){
                throw new IllegalArgumentException("A malformed range in NumberInRange filter has been provided.");
            }
            conditions.add(String.format("(%s in [%f..%f])", key, condition.get(0), condition.get(1)));
        }
        return "not(" + String.join(" or ", conditions) + ")";
    }
}
