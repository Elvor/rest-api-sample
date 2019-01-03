package org.elvor.sample.banking.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * A HTTP response library-independent representation.
 */
@RequiredArgsConstructor
@Getter
public
class Response {

    private final ResponseCode code;

    private final String data;

    private final Map<String, String> headers = new HashMap<>();
}
