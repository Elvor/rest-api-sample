package org.elvor.sample.banking.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.elvor.sample.banking.exception.ConflictException;
import org.elvor.sample.banking.exception.MethodNotAllowedException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.exception.ValidationException;
import org.elvor.sample.banking.rest.HTTPMethod;
import org.elvor.sample.banking.rest.ResponseCode;
import org.elvor.sample.banking.rest.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of {@link ExceptionTranslator}.
 */
@Slf4j
public class ExceptionTranslatorImpl implements ExceptionTranslator {

    private static final String MESSAGE_FORMAT = "{\"message\": \"%s\"}";

    private final Map<Class<? extends Throwable>, ResponseConverter> creatorMap = new HashMap<>();

    private final ResponseConverter defaultCreator = new ResponseConverter(ResponseCode.SERVER_ERROR);

    public ExceptionTranslatorImpl() {
        creatorMap.put(NotFoundException.class, new ResponseConverter(ResponseCode.NOT_FOUND));
        creatorMap.put(ConflictException.class, new ResponseConverter(ResponseCode.CONFLICT));
        creatorMap.put(ValidationException.class, new ResponseConverter(ResponseCode.BAD_REQUEST));

        creatorMap.put(
                MethodNotAllowedException.class,
                new ResponseConverter(ResponseCode.METHOD_NO_ALLOWED) {
                    @Override
                    Response createResponse(final Throwable exception) {
                        final Response response = super.createResponse(exception);
                        final String headerValue = ((MethodNotAllowedException) exception).getAllowed().stream()
                                .map(HTTPMethod::getName)
                                .collect(Collectors.joining(", "));
                        response.getHeaders().put("Allow", headerValue);
                        return response;
                    }
                });
    }

    @Override
    public Response translate(final Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        try {
            return creatorMap.getOrDefault(throwable.getClass(), defaultCreator).createResponse(throwable);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return new Response(ResponseCode.SERVER_ERROR, createMessage("Server error"));
        }

    }

    private String createMessage(final String messageContent) {
        return String.format(MESSAGE_FORMAT, messageContent.replace("\"", "\\\""));
    }


    /**
     * Converts exception to response.
     */
    private class ResponseConverter {
        private final ResponseCode responseCode;

        private ResponseConverter(final ResponseCode responseCode) {
            this.responseCode = responseCode;
        }

        Response createResponse(final Throwable throwable) throws ClassCastException {
            return new Response(
                    responseCode,
                    createMessage(throwable.getMessage())
            );
        }
    }
}
