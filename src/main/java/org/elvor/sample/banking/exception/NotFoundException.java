package org.elvor.sample.banking.exception;

/**
 * Thrown if requested entity wasn't found.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException() {
        super("Not found");
    }
}
