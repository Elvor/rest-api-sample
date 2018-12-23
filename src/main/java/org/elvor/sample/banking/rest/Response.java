package org.elvor.sample.banking.rest.dispatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.elvor.sample.banking.rest.ResponseCode;

@RequiredArgsConstructor
@Getter
public
class Response {

    private final ResponseCode code;

    private final String data;
}
