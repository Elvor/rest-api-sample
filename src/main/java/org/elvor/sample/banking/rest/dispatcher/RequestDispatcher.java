package org.elvor.sample.banking.rest.dispatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elvor.sample.banking.rest.HTTPMethod;

import java.util.Map;

/**
 * A dispatcher for incoming requests.
 */
public interface RequestDispatcher {

    void register(String path, HTTPMethod method, Handler handler);

    Result getHandler(String path, HTTPMethod httpMethod);

    /**
     * Dispatching result.
     */
    @Getter
    @AllArgsConstructor
    class Result {

        private final Handler handler;

        private final Map<String, String> pathVariables;
    }
}
