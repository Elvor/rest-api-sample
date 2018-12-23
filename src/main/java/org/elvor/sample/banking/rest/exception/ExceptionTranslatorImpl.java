package org.elvor.sample.banking.rest.exception;

import org.elvor.sample.banking.converter.Converter;
import org.elvor.sample.banking.exception.ConflictException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.exception.ValidationException;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;

import java.util.HashMap;
import java.util.Map;

public class ExceptionTranslatorImpl implements ExceptionTranslator {

    //TODO move to properties.
    private final Map<Class<? extends Throwable>, String> codeMap = new HashMap<>();
    private final Converter converter;

    public ExceptionTranslatorImpl(final Converter converter) {
        this.converter = converter;
        codeMap.put(NotFoundException.class, "404");
        codeMap.put(ConflictException.class, "409");
        codeMap.put(ValidationException.class, "400");
    }

    @Override
    public RequestDispatcher.Handler.Response handle(final Throwable throwable) {

    }

    private String getCode(final Class<? extends Throwable> exceptionClass) {
        return codeMap.getOrDefault(exceptionClass, "500");
    }
}
