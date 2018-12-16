package org.elvor.sample.banking.rest.exception;

import io.vertx.core.http.HttpServerResponse;
import org.elvor.sample.banking.converter.Converter;
import org.elvor.sample.banking.exception.ConflictException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

public class ExceptionHandlerImpl implements ExceptionHandler {

    //TODO move to properties.
    private static final String SERVER_ERROR = "{\"code\": 500, \"message\": \"Server error\"}";
    private final Map<Class<? extends Throwable>, String> codeMap = new HashMap<>();
    private final Converter converter;

    public ExceptionHandlerImpl(final Converter converter) {
        this.converter = converter;
        codeMap.put(NotFoundException.class, "404");
        codeMap.put(ConflictException.class, "409");
        codeMap.put(ValidationException.class, "400");
    }

    @Override
    public void handle(final Throwable throwable, final HttpServerResponse response) {

        final Map<String, String> responseObject = new HashMap<>();
        responseObject.put("code", getCode(throwable.getClass()));
        responseObject.put("message", throwable.getMessage());

        converter.toResponse(response, responseObject, SERVER_ERROR);
    }

    private String getCode(final Class<? extends Throwable> exceptionClass) {
        return codeMap.getOrDefault(exceptionClass, "500");
    }
}
