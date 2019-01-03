package org.elvor.sample.banking.rest.controller;

import org.elvor.sample.banking.exception.InternalErrorException;
import org.elvor.sample.banking.rest.Request;

/**
 * Controller helper.
 */
public class ControllerHelper {

    String getPathVariableValue(final Request request, final String pathVariableName) {
        final String value = request.getPathVariables().get(pathVariableName);
        if (value == null) {
            throw new InternalErrorException("Cannot find path variable " + pathVariableName);
        }
        return value;
    }
}
