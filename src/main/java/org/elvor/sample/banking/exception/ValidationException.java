package org.elvor.sample.banking.exception;

/**
 * Thrown if incoming request is malcontent.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ValidationException(final String message) {
        super(message);
    }
}
