package org.elvor.sample.banking.exception;

/**
 * Thrown in case of conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConflictException(final String message) {
        super(message);
    }
}
