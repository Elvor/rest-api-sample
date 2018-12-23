package org.elvor.sample.banking.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A HTTP response library-independent representation.
 */
@RequiredArgsConstructor
@Getter
public
class Response {

    private final ResponseCode code;

    private final String data;
}
