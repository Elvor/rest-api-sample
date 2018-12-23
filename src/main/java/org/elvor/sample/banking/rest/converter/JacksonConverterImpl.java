package org.elvor.sample.banking.rest.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elvor.sample.banking.exception.InternalErrorException;
import org.elvor.sample.banking.exception.ValidationException;
import org.elvor.sample.banking.rest.ResponseCode;
import org.elvor.sample.banking.rest.Request;
import org.elvor.sample.banking.rest.Response;

import java.io.IOException;

/**
 * Jackson base inplementation of {@link Converter}.
 */
public class ConverterImpl implements Converter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T fromRequest(final Request request, Class<T> resultClass) {
        if (request.getData() == null) {
            return null;
        }
        try {
            return objectMapper.readValue(request.getData(), resultClass);
        } catch (IOException e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    @Override
    public <T> Response toResponse(final T object) {
        return toResponse(object, ResponseCode.SUCCESSFUL);
    }

    @Override
    public <T> Response toResponse(final T object, final ResponseCode code) {
        if (object == null) {
            return new Response(code, "");
        }
        try {
            return new Response(code, objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new InternalErrorException(e.getMessage(), e);
        }
    }
}
