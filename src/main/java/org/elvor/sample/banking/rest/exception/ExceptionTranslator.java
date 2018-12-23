package org.elvor.sample.banking.rest.exception;

import org.elvor.sample.banking.rest.Response;

/**
 * An exception translator to response.
 */
public interface ExceptionTranslator {
    Response translate(Throwable throwable);
}
