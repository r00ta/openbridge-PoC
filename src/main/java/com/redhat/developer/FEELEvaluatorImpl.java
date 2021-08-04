package com.redhat.developer;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.developer.models.FilterType;
import org.kie.dmn.feel.FEEL;

@ApplicationScoped
public class FEELEvaluatorImpl implements FEELEvaluator {
    private static final String IS_VALID = "OK";
    private static final String IS_INVALID = "NOT_OK";

    private static final Map<FilterType, String> filterMap;

    private static final String TEMPLATE = "if %s %s %s then \"" + IS_VALID + "\" else \"" + IS_INVALID + "\"";

    private static final FEEL feel = FEEL.newInstance();

    static {
        filterMap = new HashMap<>();
        filterMap.put(FilterType.IN, "in");
        filterMap.put(FilterType.NOT_IN, "not in ");
    }

    @Override
    public String buildFilter(String key, String operator, String value){
        return String.format(TEMPLATE, key, operator, value);
    }

    @Override
    public boolean evaluateFilter(String template, Map<String, Object> data){
        Object result = feel.evaluate(template, data);
        return result.equals(IS_VALID);
    }

    @Override
    public String convertType(FilterType filterType) {
        return filterMap.get(filterType);
    }
}
