package org.elvor.sample.banking.rest.controller;

import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.rest.PathVariableHelper;
import org.elvor.sample.banking.rest.converter.Converter;
import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.rest.HTTPMethod;
import org.elvor.sample.banking.rest.ResponseCode;
import org.elvor.sample.banking.rest.Request;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;
import org.elvor.sample.banking.rest.Response;
import org.elvor.sample.banking.service.AccountService;
import org.elvor.sample.banking.service.vo.TransferInfo;


/**
 * Controller for accounts.
 */
public class AccountController {

    private static final String PATH_VAR_ID = "id";

    private final RequestDispatcher requestDispatcher;

    private final AccountService accountService;

    private final Converter converter;

    private final ControllerHelper controllerHelper = new ControllerHelper();

    private final PathVariableHelper pathVariableHelper = new PathVariableHelper();

    public AccountController(final RequestDispatcher requestDispatcher, final AccountService accountService,
                             final Converter converter) {
        this.requestDispatcher = requestDispatcher;
        this.accountService = accountService;
        this.converter = converter;
    }

    public void init() {
        requestDispatcher.register("", HTTPMethod.POST, this::register);
        requestDispatcher.register("transfer", HTTPMethod.POST, this::transfer);
        requestDispatcher.register("", HTTPMethod.GET, this::fetch);
        requestDispatcher.register(pathVariableHelper.createPathVariable(PATH_VAR_ID), HTTPMethod.DELETE, this::delete);
        requestDispatcher.register(pathVariableHelper.createPathVariable(PATH_VAR_ID), HTTPMethod.GET, this::fetchOne);
    }

    private Response delete(final Request request) {
        accountService.delete(getId(request));
        return converter.toResponse(null, ResponseCode.NO_CONTENT);
    }

    private Response fetch(final Request request) {
        return converter.toResponse(accountService.findAll());
    }

    private Response fetchOne(final Request request) {
        final Account found = accountService.find(getId(request)).orElseThrow(NotFoundException::new);
        return converter.toResponse(found);
    }

    private Long getId(final Request request) {
        return Long.parseLong(controllerHelper.getPathVariableValue(request, PATH_VAR_ID));
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
