package org.elvor.sample.banking.rest.dispatcher;

import org.elvor.sample.banking.rest.Request;
import org.elvor.sample.banking.rest.Response;

/**
 * A specific REST query handler.
 */
public interface Handler {
    Response handle(Request request);

}
