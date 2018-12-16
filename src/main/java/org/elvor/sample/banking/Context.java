package org.elvor.sample.banking;


import lombok.Getter;
import org.elvor.sample.banking.converter.Converter;
import org.elvor.sample.banking.converter.ConverterImpl;
import org.elvor.sample.banking.repository.AccountRepository;
import org.elvor.sample.banking.repository.AccountRepositoryMapImpl;
import org.elvor.sample.banking.rest.controller.AccountController;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcherImpl;
import org.elvor.sample.banking.rest.exception.ExceptionHandler;
import org.elvor.sample.banking.rest.exception.ExceptionHandlerImpl;
import org.elvor.sample.banking.service.AccountService;
import org.elvor.sample.banking.service.AccountServiceImpl;

/**
 * Application's context. The simplest replacement for DI. Wire everything yourself.
 */
@Getter
public class Context {

    @Getter
    private static Context context = new Context();

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final RequestDispatcher requestDispatcher;

    private final ExceptionHandler exceptionHandler;

    private final Converter converter;

    private final AccountController accountController;

    private Context() {
        accountRepository = new AccountRepositoryMapImpl();
        accountService = new AccountServiceImpl(accountRepository);
        converter = new ConverterImpl();
        exceptionHandler = new ExceptionHandlerImpl(converter);
        requestDispatcher = new RequestDispatcherImpl(exceptionHandler);
        accountController = new AccountController(requestDispatcher, accountService, converter);
        accountController.init();
    }
}
