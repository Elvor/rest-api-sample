package org.elvor.sample.banking.rest.dispatcher.impl;

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
        Node node = root;
        for (String pathPart : pathParts) {
            final boolean isVariable = pathVariableHelper.isPathVariable(pathPart);
            final Node next = node.getChild(pathPart, isVariable);
            if (next == null) {
                node = node.addChild(pathPart, isVariable);
            } else {
                node = next;
            }
        }
        node.addHandler(method, handler);

    }

    @Override
    public Result getHandler(final String path, final HTTPMethod httpMethod) {
        final List<String> pathParts = extractPathParts(path);
        final Map<String, String> params = new HashMap<>();
        Node node = root;
        for (String pathPart : pathParts) {
            node = node.getChildNullSafe(pathPart, params);
        }
        return new Result(node.getHandler(httpMethod), params);
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
        private final Map<HTTPMethod, Handler> handlers = new HashMap<>();
        private final String path;
        private final Node parent;
        private final Map<String, Node> children = new HashMap<>();
        private Node variable;

        Node getChild(final String path, final boolean isVariable) {
            if (isVariable) {
                return this.variable;
            }
            return children.get(path);
        }

        Handler getHandler(final HTTPMethod method) {
            final Handler handler = handlers.get(method);
            if (handler == null) {
                throw new MethodNotAllowedException(handlers.keySet());
            }
            return handler;
        }

        Node getChildNullSafe(final String pathPart, final Map<String, String> params) {
            Node node = children.get(pathPart);
            if (node == null) {
                if (variable == null) {
                    throw new NotFoundException("Route wasn't found");
                }
                node = variable;
                params.put(variable.path, pathPart);
            }
            return node;
        }

        Node addChild(final String path, final boolean isVariable) {
            final Node node = new Node(path, this);
            if (isVariable) {
                final String paramName = pathVariableHelper.extractPathVariableName(path);
                variable = new Node(paramName, this);
                return variable;
            }
            children.put(path, node);
            return node;
        }

        void addHandler(final HTTPMethod httpMethod, final Handler handler) {
            if (handlers.get(httpMethod) != null) {
                throw new InitializationException(String.format(
                        "method %s %s is already registered",
                        httpMethod.getName(),
                        getFullPath()
                ));
            }
            handlers.put(httpMethod, handler);
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
}
