package org.elvor.sample.banking.rest.dispatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public
class Request {

    private final String data;

    private final Map<String, List<String>> parameters;

    private final Map<String, String> headers;
}
