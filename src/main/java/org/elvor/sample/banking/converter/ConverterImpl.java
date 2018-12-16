package org.elvor.sample.banking.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.elvor.sample.banking.exception.InternalErrorException;
import org.elvor.sample.banking.exception.ValidationException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ConverterImpl implements Converter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T fromRequest(final HttpServerRequest request, final Class<T> resultClass) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        request.handler(body -> {
            try {
                final T result = objectMapper.readValue(body.toString(), resultClass);
                future.complete(result);
            } catch (IOException e) {
                throw new ValidationException(e.getMessage(), e);
            }
        });
        return future.join();
    }

    @Override
    public <T> void toResponse(final HttpServerResponse response, final T object) {
        toResponse(response, object, null);
    }

    @Override
    public <T> void toResponse(final HttpServerResponse response, final T object, final String errorObject) {
        response.putHeader("Content-Type", "application/json");

        String content;
        try {
            content = objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            if (errorObject == null) {
                throw new InternalErrorException(e.getMessage(), e);
            }
            content = errorObject;
        }
        defineContentLength(response, content.length());
        response.write(content);
        response.end();
    }

    private void defineContentLength(final HttpServerResponse response, final int length) {
        response.putHeader("Content-Length", String.valueOf(length));
    }
}
