package org.elvor.sample.banking.rest.dispatcher.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.elvor.sample.banking.exception.InitializationException;
import org.elvor.sample.banking.exception.MethodNotAllowedException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.rest.HTTPMethod;
import org.elvor.sample.banking.rest.PathVariableHelper;
import org.elvor.sample.banking.rest.dispatcher.Handler;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * A tree implementation of {@link RequestDispatcher}.
 */
@RequiredArgsConstructor
public class RequestDispatcherTreeImpl implements RequestDispatcher {

    private final Node root = new Node("", null);

    private final PathVariableHelper pathVariableHelper = new PathVariableHelper();

    @Override
    public void register(final String path, final HTTPMethod method, final Handler handler) {
        final List<String> pathParts = extractPathParts(path);
        final RequestRegistrationContext regContext = new RequestRegistrationContext(handler, path);
        Node node = root;
        for (String pathPart : pathParts) {
            final boolean isVariable = pathVariableHelper.isPathVariable(pathPart);
            if (isVariable) {
                regContext.pathVariablesNames.add(pathVariableHelper.extractPathVariableName(pathPart));
            }
            final Node next = node.getChild(pathPart, isVariable);
            if (next == null) {
                node = node.addChild(pathPart, isVariable);
            } else {
                node = next;
            }
        }
        node.addHandler(method, regContext);

    }

    @Override
    public Result getHandler(final String path, final HTTPMethod httpMethod) {
        final List<String> pathParts = extractPathParts(path);
        final List<String> variablesValues = new LinkedList<>();
        Node node = root;
        for (String pathPart : pathParts) {
            node = node.getChildNullSafe(pathPart, variablesValues);
        }
        return node.getHandlerData(httpMethod).toResult(variablesValues);
    }

    private List<String> extractPathParts(final String path) {
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Path tree node.
     */
    @RequiredArgsConstructor
    private class Node {
        private final Map<HTTPMethod, HandlerData> handlers = new HashMap<>();
        private final String path;
        private final Node parent;
        private final Map<String, Node> children = new HashMap<>();
        private Node variable;

        Node getChild(final String pathPart, final boolean isVariable) {
            if (isVariable) {
                return this.variable;
            }
            return children.get(pathPart);
        }

        HandlerData getHandlerData(final HTTPMethod method) {
            final HandlerData handlerData = handlers.get(method);
            if (handlerData == null) {
                throw new MethodNotAllowedException(handlers.keySet());
            }
            return handlerData;
        }

        Node getChildNullSafe(final String pathPart, final List<String> variablesValues) {
            Node node = children.get(pathPart);
            if (node == null) {
                if (variable == null) {
                    throw new NotFoundException("Route wasn't found");
                }
                node = variable;
                variablesValues.add(pathPart);
            }
            return node;
        }

        Node addChild(final String path, final boolean isVariable) {
            final Node node = new Node(path, this);
            if (isVariable) {
                variable = new Node("VARIABLE", this);
                return variable;
            }
            children.put(path, node);
            return node;
        }

        void addHandler(final HTTPMethod httpMethod, final RequestRegistrationContext regContext) {
            if (handlers.get(httpMethod) != null) {
                throw new InitializationException(String.format(
                        "method %s %s is already registered",
                        httpMethod.getName(),
                        regContext.getPath()
                ));
            }
            handlers.put(httpMethod, regContext.toHandlerData());
        }

        String getFullPath() {
            Node parent = this.parent;
            final Deque<String> deque = new LinkedList<>();
            do {
                deque.addFirst(path);
                parent = parent.parent;
            }
            while (parent != null);
            return String.join("/", deque);
        }
    }

    /**
     * Context for registration process.
     */
    @RequiredArgsConstructor
    private class RequestRegistrationContext {

        private final Handler handler;

        @Getter
        private final String path;

        private final List<String> pathVariablesNames = new LinkedList<>();

        HandlerData toHandlerData() {
            return new HandlerData(handler, pathVariablesNames);
        }
    }

    /**
     * Handler with all additional data.
     */
    @RequiredArgsConstructor
    private class HandlerData {

        private final Handler handler;

        private final List<String> pathVariablesNames;

        Result toResult(final List<String> pathVariableValues) {
            final Map<String, String> pathVariables = new HashMap<>();
            final Iterator<String> namesIterator = pathVariablesNames.iterator();
            final Iterator<String> valuesIterator = pathVariableValues.iterator();
            while (namesIterator.hasNext()) {
                pathVariables.put(namesIterator.next(), valuesIterator.next());
            }
            return new Result(handler, pathVariables);
        }
    }
}
