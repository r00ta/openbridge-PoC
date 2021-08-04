package com.redhat.developer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.runtime.functions.NotFunction;
import org.kie.dmn.feel.runtime.functions.StartsWithFunction;

public class FilterTest {

    @Test
    public void testIN(){
        String expr = "if\n" +
                "list contains ([\"jacopo\", \"marco\"], data.name)\n" +
                "then\n" +
                "    \"valid\"\n" +
                "else\n" +
                "    \"not valid\"";
        FEEL feel = FEEL.newInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("data", Collections.singletonMap("name", "jacopo"));
        Object result = feel.evaluate(expr, map);
        Assertions.assertEquals("valid", result);
    }

    @Test
    public void testNOTIN(){
        String expr = "if\n" +
                "not (list contains ([\"jacopo\"], data.name))\n" +
                "then\n" +
                "    \"valid\"\n" +
                "else\n" +
                "    \"not valid\"";
        FEEL feel = FEEL.newInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("data", Collections.singletonMap("name", "jacgfopo"));
        Object result = feel.evaluate(expr, map);
        Assertions.assertEquals("valid", result);
    }

    @Test
    public void startsWith(){
        String expr = "if\n" +
                "starts with (data.name, \"jacopo\")\n" +
                "then\n" +
                "    \"valid\"\n" +
                "else\n" +
                "    \"not valid\"";
        FEEL feel = FEEL.newInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("data", Collections.singletonMap("name", "jacopoaaaaaaaaaa"));
        Object result = feel.evaluate(expr, map);
        Assertions.assertEquals("valid", result);
    }

    @Test
    public void contains(){
        String expr = "if\n" +
                "contains (data.name, \"jacopo\")\n" +
                "then\n" +
                "    \"valid\"\n" +
                "else\n" +
                "    \"not valid\"";
        FEEL feel = FEEL.newInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("data", Collections.singletonMap("name", "ciao sono jacopo"));
        Object result = feel.evaluate(expr, map);
        Assertions.assertEquals("valid", result);
    }


    @Test
    public void equals(){
        String expr = "if\n" +
                "data.name = \"jacopo\"\n" +
                "then\n" +
                "    \"valid\"\n" +
                "else\n" +
                "    \"not valid\"";
        FEEL feel = FEEL.newInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("data", Collections.singletonMap("name", "jacopo"));
        Object result = feel.evaluate(expr, map);
        Assertions.assertEquals("valid", result);
    }

}
