package com.redhat.developer.models.filters;

import javax.persistence.Embeddable;

@Embeddable
public class StringNotEndsWith extends Filter {
    public static final String FILTER_TYPE_NAME = "StringNotEndsWith";
    private final String type = FILTER_TYPE_NAME;

    @Override
    public String getConditionTemplate() {
        return null;
    }
}
