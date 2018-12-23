package org.elvor.sample.banking.rest.dispatcher;

import org.elvor.sample.banking.exception.InitializationException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.rest.HTTPMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * The simplest implementation based on map. Doesn't support path variables.
 */
public class RequestDispatcherMapImpl implements RequestDispatcher {

    private Map<String, Map<HTTPMethod, Handler>> requestMap = new HashMap<>();

    @Override
    public void register(final String path, final HTTPMethod method, final Handler handler) {
        Map<HTTPMethod, Handler> methodMap = this.requestMap.get(path);
        if (methodMap == null) {
            methodMap = new HashMap<>();
            requestMap.put(path, methodMap);
        }

        methodMap.computeIfPresent(method, (k, v) ->  {
            throw createMethodRegistrationException(path, method);
        });
        methodMap.put(method, handler);
    }

    private RuntimeException createMethodRegistrationException(final String path, final HTTPMethod method) {
        return new InitializationException(String.format(
                "Request handler with method %s and path %s already registered",
                method.getName(),
                path
        ));
    }

    private RuntimeException createNotFoundException(final String path, final HTTPMethod method) {
        return new NotFoundException(String.format("Not found %s %s", method.getName(), path));
    }

    public Handler getHandler(final String path, final HTTPMethod method) {
        final Map<HTTPMethod, Handler> methodMap = requestMap.get(path);
        if (methodMap == null) {
            throw createNotFoundException(path, method);
        }
        methodMap.computeIfAbsent(method, v -> {
            throw createNotFoundException(path, method);
        });
        return methodMap.get(method);
    }


}
