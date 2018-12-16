package org.elvor.sample.banking.converter;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public interface Converter {
    <T> T fromRequest(HttpServerRequest request, Class<T> resultClass);

    <T> void toResponse(HttpServerResponse response, T object);

    <T> void toResponse(HttpServerResponse response, T object, String errorObject);
}
