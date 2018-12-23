package org.elvor.sample.banking.exception;

/**
 * Thrown if internal server error occurred.
 */
public class InternalErrorException extends RuntimeException {

    public InternalErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InternalErrorException(final String message) {
        super(message);
    }
}
