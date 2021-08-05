package com.redhat.developer;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import com.redhat.developer.models.filters.FiltersConstants;
import org.kie.dmn.feel.FEEL;

@ApplicationScoped
public class FEELEvaluatorImpl implements FEELEvaluator {
    private static final FEEL feel = FEEL.newInstance();

    @Override
    public boolean evaluateFilter(String template, Map<String, Object> data){
        System.out.println("evaluating " + template + " with " + data.toString());
        Object result = feel.evaluate(template, data);
        return result.equals(FiltersConstants.IS_VALID);
    }
}
