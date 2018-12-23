package org.elvor.sample.banking.rest.dispatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.elvor.sample.banking.rest.ResponseCode;

import java.util.List;
import java.util.Map;

public interface Handler {
    Response handle(Request request);

    @RequiredArgsConstructor
    @Getter
    class Request {

        private final String data;

        private final Map<String, List<String>> parameters;

        private final Map<String, String> headers;
    }

    @RequiredArgsConstructor
    @Getter
    class Response {

        private final ResponseCode code;

        private final String data;
    }
}
