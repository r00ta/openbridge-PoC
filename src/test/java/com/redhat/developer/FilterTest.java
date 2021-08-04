package com.redhat.developer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.FEEL;

public class FilterTest {

    @Test
    public void test(){
        String expr = "if\n" +
                "data.name in (\"jacopo\")\n" +
                "then\n" +
                "    \"valid\"\n" +
                "else\n" +
                "    \"not valid\"";
        FEEL feel = FEEL.newInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("data", Collections.singletonMap("name", "jacgfopo"));
        Object result = feel.evaluate(expr, map);
        System.out.println(result);
    }
}
