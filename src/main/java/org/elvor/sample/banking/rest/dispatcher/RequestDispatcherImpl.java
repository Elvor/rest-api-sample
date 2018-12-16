package org.elvor.sample.banking.rest.dispatcher;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import org.elvor.sample.banking.exception.InitializationException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.rest.exception.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public class RequestDispatcherImpl implements RequestDispatcher {

    private final ExceptionHandler exceptionHandler;

    private Map<String, Map<HttpMethod, Handler<HttpServerRequest>>> requestMap = new HashMap<>();

    public RequestDispatcherImpl(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void register(final String path, final HttpMethod method, final Handler<HttpServerRequest> handler) {
        Map<HttpMethod, Handler<HttpServerRequest>> methodMap = this.requestMap.get(path);
        if (methodMap == null) {
            methodMap = new HashMap<>();
            requestMap.put(path, methodMap);
        }

        methodMap.computeIfPresent(method, (k, v) ->  {
            throw createMethodRegistrationException(path, method.name());
        });
        methodMap.put(method, handler);
    }

    private RuntimeException createMethodRegistrationException(final String path, final String method) {
        return new InitializationException(String.format(
                "Request handler with method %s and path %s already registered",
                method,
                path
        ));
    }

    @Override
    public void handle(final HttpServerRequest event) {
        try {
            final String path = event.path();
            final HttpMethod method = event.method();
            final Map<HttpMethod, Handler<HttpServerRequest>> methodMap = requestMap.get(path);
            if (methodMap == null) {
                throw createNotFoundException(path, method);
            }
            methodMap.computeIfAbsent(method, v -> {
                throw createNotFoundException(path, method);
            });
            methodMap.get(method).handle(event);
        } catch (final RuntimeException e) {
            exceptionHandler.handle(e, event.response());
        }
    }

    private RuntimeException createNotFoundException(final String path, final HttpMethod method) {
        return new NotFoundException(String.format("Not found %s %s", method.name(), path));
    }
}
