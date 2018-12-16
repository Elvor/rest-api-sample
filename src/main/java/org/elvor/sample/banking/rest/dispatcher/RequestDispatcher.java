package org.elvor.sample.banking.rest.dispatcher;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;

public interface RequestDispatcher extends Handler<HttpServerRequest> {

    void register(String path, HttpMethod method, Handler<HttpServerRequest> handler);

    void handle(HttpServerRequest event);
}
