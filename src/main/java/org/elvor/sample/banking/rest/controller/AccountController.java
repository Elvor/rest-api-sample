package org.elvor.sample.banking.rest.controller;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import org.elvor.sample.banking.converter.Converter;
import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;
import org.elvor.sample.banking.service.AccountService;
import org.elvor.sample.banking.vo.TransferInfo;

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
        requestDispatcher.register("/register", HttpMethod.POST, this::register);
        requestDispatcher.register("/transfer", HttpMethod.POST, this::transfer);
    }

    private void register(final HttpServerRequest request) {
        final Account account = converter.fromRequest(request, Account.class);
        final Account created = accountService.create(account);
        request.response().setStatusCode(201);
        converter.toResponse(request.response(), created);
    }

    private void transfer(final HttpServerRequest request) {
        final TransferInfo transferInfo = converter.fromRequest(request, TransferInfo.class);
        accountService.transfer(transferInfo);
        converter.toResponse(request.response(), transferInfo);
    }
}
