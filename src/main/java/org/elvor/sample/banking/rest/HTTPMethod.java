package org.elvor.sample.banking.rest;


import java.util.HashMap;
import java.util.Map;

/**
 * Library independent method names.
 */
public class HTTPMethod {
    public static final HTTPMethod POST = new HTTPMethod("POST");
    public static final HTTPMethod GET = new HTTPMethod("GET");
    public static final HTTPMethod DELETE = new HTTPMethod("DELETE");
    public static final HTTPMethod PUT = new HTTPMethod("PUT");

    private static final Map<String, HTTPMethod> METHOD_MAP;

    static {
        METHOD_MAP = new HashMap<>();
        METHOD_MAP.put(POST.getName(), POST);
        METHOD_MAP.put(DELETE.getName(), DELETE);
        METHOD_MAP.put(GET.getName(), GET);
        METHOD_MAP.put(PUT.getName(), PUT);
    }

    public static HTTPMethod valueOf(final String name) {
        return METHOD_MAP.get(name);
    }

    private final String name;

    HTTPMethod(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
