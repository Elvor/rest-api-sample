package org.elvor.sample.banking.rest.controller;

import org.elvor.sample.banking.rest.converter.Converter;
import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.rest.HTTPMethod;
import org.elvor.sample.banking.rest.ResponseCode;
import org.elvor.sample.banking.rest.Request;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;
import org.elvor.sample.banking.rest.Response;
import org.elvor.sample.banking.service.AccountService;
import org.elvor.sample.banking.service.vo.TransferInfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for accounts.
 */
public class AccountController {

    private final RequestDispatcher requestDispatcher;

    private final AccountService accountService;

    private final Converter converter;

    public AccountController(final RequestDispatcher requestDispatcher, final AccountService accountService,
                             final Converter converter) {
        this.requestDispatcher = requestDispatcher;
        this.accountService = accountService;
        this.converter = converter;
    }

    public void init() {
        requestDispatcher.register("/", HTTPMethod.POST, this::register);
        requestDispatcher.register("/transfer", HTTPMethod.POST, this::transfer);
        requestDispatcher.register("/", HTTPMethod.GET, this::fetch);
        requestDispatcher.register("/", HTTPMethod.DELETE, this::delete);
    }

    private Response delete(final Request request) {
        accountService.deleteAll(extractIds(request));
        return converter.toResponse(null, ResponseCode.NO_CONTENT);
    }

    private Response fetch(final Request request) {
        return converter.toResponse(accountService.findAll(extractIds(request)));
    }

    private List<Long> extractIds(final Request request) {
        final List<String> ids = request.getParameters().get("id");
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    private Response register(final Request request) {
        final Account account = converter.fromRequest(request, Account.class);
        final Account created = accountService.create(account);
        return converter.toResponse(created, ResponseCode.CREATED);
    }

    private Response transfer(final Request request) {
        final TransferInfo transferInfo = converter.fromRequest(request, TransferInfo.class);
        final TransferInfo result = accountService.transfer(transferInfo);
        return converter.toResponse(result);
    }
}
