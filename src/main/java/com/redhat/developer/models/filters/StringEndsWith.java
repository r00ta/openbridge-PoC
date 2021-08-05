package com.redhat.developer.models.filters;

import javax.persistence.Embeddable;

@Embeddable
public class StringEndsWith extends Filter {
    public static final String FILTER_TYPE_NAME = "StringEndsWith";
    private final String type = FILTER_TYPE_NAME;

    @Override
    public String getConditionTemplate() {
        return null;
    }
}
