package org.elvor.sample.banking.exception;

public class InitializationException extends RuntimeException {

    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InitializationException(final String message) {
        super(message);
    }
}
