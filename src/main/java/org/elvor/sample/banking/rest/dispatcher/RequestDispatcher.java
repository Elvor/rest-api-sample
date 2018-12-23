package org.elvor.sample.banking.rest.dispatcher;

import org.elvor.sample.banking.rest.HTTPMethod;

/**
 * A dispatcher for incoming requests.
 */
public interface RequestDispatcher {

    void register(String path, HTTPMethod method, Handler handler);

    Handler getHandler(String path, HTTPMethod httpMethod);
}
