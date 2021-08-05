package com.redhat.developer;

import java.util.Map;

public interface FEELEvaluator {
    boolean evaluateFilter(String template, Map<String, Object> data);
}
