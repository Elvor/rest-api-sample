package org.elvor.sample.banking.rest.converter;


import org.elvor.sample.banking.rest.ResponseCode;
import org.elvor.sample.banking.rest.Request;
import org.elvor.sample.banking.rest.Response;

/**
 * Converter between request date and entity.
 */
public interface Converter {
    <T> T fromRequest(final Request request, Class<T> resultClass);

    <T> Response toResponse(T object, ResponseCode code);

    <T> Response toResponse(T object);

}
