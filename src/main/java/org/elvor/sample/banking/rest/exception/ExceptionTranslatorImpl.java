package org.elvor.sample.banking.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.elvor.sample.banking.exception.ConflictException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.exception.ValidationException;
import org.elvor.sample.banking.rest.ResponseCode;
import org.elvor.sample.banking.rest.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link ExceptionTranslator}.
 */
@Slf4j
public class ExceptionTranslatorImpl implements ExceptionTranslator {

    private static final String MESSAGE_FORMAT = "{\"message\": \"%s\"}";

    private final Map<Class<? extends Throwable>, ResponseCode> codeMap = new HashMap<>();

    public ExceptionTranslatorImpl() {
        codeMap.put(NotFoundException.class, ResponseCode.NOT_FOUND);
        codeMap.put(ConflictException.class, ResponseCode.CONFLICT);
        codeMap.put(ValidationException.class, ResponseCode.BAD_REQUEST);
    }

    @Override
    public Response translate(final Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return new Response(
                getCode(throwable.getClass()),
                String.format(MESSAGE_FORMAT, throwable.getMessage().replace("\"", "\\\""))
        );
    }

    private ResponseCode getCode(final Class<? extends Throwable> exceptionClass) {
        return codeMap.getOrDefault(exceptionClass, ResponseCode.SERVER_ERROR);
    }
}
