package org.elvor.sample.banking.rest;


/**
 * Helper for path variables.
 */
public class PathVariableHelper {
    private static final String LABEL = ":";

    public boolean isPathVariable(final String pathPart) {
        return pathPart.startsWith(LABEL);
    }

    public String extractPathVariableName(final String pathPart) {
        return pathPart.substring(LABEL.length());
    }

    public String createPathVariable(final String pathVariableName) {
        return LABEL + pathVariableName;
    }
}
