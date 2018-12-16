package org.elvor.sample.banking;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

/**
 * Application class.
 */
public class App {
    public static void main(final String[] args) {

        final Context context = Context.getContext();
        final Vertx vertx = Vertx.vertx();
        final HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(context.getRequestDispatcher());
        httpServer.listen(8080);
        System.out.println("Server started");
    }
}
