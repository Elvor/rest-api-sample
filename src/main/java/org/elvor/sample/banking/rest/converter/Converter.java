package org.elvor.sample.banking.converter;


import org.elvor.sample.banking.rest.ResponseCode;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;

public interface Converter {
    <T> T fromRequest(final RequestDispatcher.Handler.Request request, Class<T> resultClass);

    <T> RequestDispatcher.Handler.Response toResponse(T object, ResponseCode code);

    <T> RequestDispatcher.Handler.Response toResponse(T object);

}
