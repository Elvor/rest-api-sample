package org.elvor.sample.banking.exception;

import lombok.Getter;
import org.elvor.sample.banking.rest.HTTPMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Thrown if requested method is not allowed.
 */
public class MethodNotAllowedException extends RuntimeException {

    @Getter
    private final List<HTTPMethod> allowed;

    public MethodNotAllowedException(final Collection<HTTPMethod> allowed) {
        super("Method not allowed");
        this.allowed = new ArrayList<>(allowed);
    }
}
