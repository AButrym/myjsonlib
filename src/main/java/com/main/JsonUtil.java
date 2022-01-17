package com.main;

import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonUtil {
    private JsonUtil() {
    }

    public static String stringify(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof String) {
            return '"' + o.toString() + '"';
        } else if (o instanceof Number) {
            return String.valueOf(o);
        } else if (o instanceof Boolean) {
            return String.valueOf(o);
        }

        final Class<?> clazz = o.getClass();
        if (!clazz.isAnnotationPresent(JSON.class)) {
            throw new IllegalArgumentException(
                    "The class should be annotated with @JSON");
        }

        StringJoiner res = new StringJoiner(", ", "{", "}");
        try {
            for (var field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                res.add(field.getName() + " : " + stringify(field.get(o)));
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public static <T> T parse(String json, Class<T> clazz) {
        if (!clazz.isAnnotationPresent(JSON.class)) {
            throw new IllegalArgumentException(
                    "The class should be annotated with @JSON");
        }
        T res = null;
        try {
            var cntr = clazz.getDeclaredConstructor();
            cntr.setAccessible(true);
            res = cntr.newInstance();

            Map<String, String> jsonMap = parseToMap(json);
            label1:
            for (var entry : jsonMap.entrySet()) {
                String atrName = entry.getKey();
                for (var method : clazz.getDeclaredMethods()) {
                    String methodName = method.getName();
                    if (methodName.startsWith("set") &&
                            atrName.equalsIgnoreCase(methodName.substring(3))) {
                        method.setAccessible(true);
                        method.invoke(res,
                                parsePrimitive(entry.getValue()));
                        continue label1;
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return res;
    }

    static Object parsePrimitive(String json) {
        json = json.trim();
        if (json.matches("[\\{\\[].*")) {
            throw new IllegalArgumentException("Not primitive json!");
        }
        switch (json) {
            case "null": return null;
            case "false": return false;
            case "true":
            case "True": return true;
        }
        if (json.matches("\\\".*\\\"")) {
            return json.substring(1, json.length() - 1);
        }
        try {
            return Integer.valueOf(json);
        } catch (NumberFormatException e) {
            // ignore
        }
        try {
            return Double.valueOf(json);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Can't parse as primitive: " + json);
        }
    }


    static <T> Map<String, String> parseToMap(String json) {
        json = json.trim();
        if (!json.startsWith("{")) {
            throw new IllegalArgumentException("Illegal json object: " + json);
        }
        json = json.substring(1, json.length() - 1);
        return Pattern.compile(",").splitAsStream(json)
                .map(String::trim)
                .map(keyval -> keyval.split(":"))
                .collect(Collectors.toMap(
                        keyval -> keyval[0].trim(),
                        keyval -> keyval[1].trim()
                ));
    }


}
