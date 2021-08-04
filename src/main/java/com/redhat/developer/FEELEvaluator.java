package com.redhat.developer;

import java.util.Map;

import com.redhat.developer.models.FilterType;

public interface FEELEvaluator {

    String buildFilter(String key, String operator, String value);

    boolean evaluateFilter(String template, Map<String, Object> data);

    String convertType(FilterType filterType);
}
