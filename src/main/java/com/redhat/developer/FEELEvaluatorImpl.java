package com.redhat.developer;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotSupportedException;

import com.redhat.developer.models.FilterType;
import org.kie.dmn.feel.FEEL;

@ApplicationScoped
public class FEELEvaluatorImpl implements FEELEvaluator {
    private static final String IS_VALID = "OK";
    private static final String IS_INVALID = "NOT_OK";

    private static final String TEMPLATE = "if %s then \"" + IS_VALID + "\" else \"" + IS_INVALID + "\"";

    private static final FEEL feel = FEEL.newInstance();

    @Override
    public String buildFilter(String key, FilterType filterType, String value){
        String expression = buildExpression(key, filterType, value);
        return String.format(TEMPLATE, expression);
    }

    @Override
    public boolean evaluateFilter(String template, Map<String, Object> data){
        Object result = feel.evaluate(template, data);
        return result.equals(IS_VALID);
    }

    private String buildExpression(String key, FilterType filterType, String value){
        switch (filterType){
            case IN:
                return String.format("list contains(%s, %s)", value, key);
            case NOT_IN:
                return String.format("not(list contains(%s, %s))", value, key);
            case CONTAINS:
                return String.format("contains(%s, \"%s\")", key, value);
            case NOT_CONTAINS:
                return String.format("not(contains(%s, \"%s\"))", key, value);
            case EQUALS:
                return String.format("%s = \"%s\"", key, value);
            default:
                throw new NotSupportedException("filter not supported");
        }
    }
}
