package org.elvor.sample.banking.exception;

/**
 * Thrown if server failed to start.
 */
public class InitializationException extends RuntimeException {

    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InitializationException(final String message) {
        super(message);
    }
}
