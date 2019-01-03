package org.elvor.sample.banking.rest;

/**
 * Library independent http response codes.
 */
public enum ResponseCode {
    SUCCESSFUL(200),
    CREATED(201),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    METHOD_NO_ALLOWED(405),
    CONFLICT(409),
    SERVER_ERROR(500);

    private final int value;

    ResponseCode(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
