package org.elvor.sample.banking.rest.exception;

import io.vertx.core.http.HttpServerResponse;

public interface ExceptionHandler {
    void handle(Throwable throwable, HttpServerResponse response);
}
