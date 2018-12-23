package org.elvor.sample.banking;


import lombok.Getter;
import org.elvor.sample.banking.rest.converter.Converter;
import org.elvor.sample.banking.rest.converter.JacksonConverterImpl;
import org.elvor.sample.banking.repository.AccountRepository;
import org.elvor.sample.banking.repository.AccountRepositoryMapImpl;
import org.elvor.sample.banking.rest.controller.AccountController;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcherMapImpl;
import org.elvor.sample.banking.rest.exception.ExceptionTranslator;
import org.elvor.sample.banking.rest.exception.ExceptionTranslatorImpl;
import org.elvor.sample.banking.rest.netty.LogicInboundHandlerAdapter;
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

    private final LogicInboundHandlerAdapter logicInboundHandlerAdapter;

    private final ExceptionTranslator exceptionTranslator;

    private final Converter converter;

    private final AccountController accountController;

    private Context() {
        accountRepository = new AccountRepositoryMapImpl();
        accountService = new AccountServiceImpl(accountRepository);
        converter = new JacksonConverterImpl();
        exceptionTranslator = new ExceptionTranslatorImpl();
        requestDispatcher = new RequestDispatcherMapImpl();
        accountController = new AccountController(requestDispatcher, accountService, converter);
        logicInboundHandlerAdapter = new LogicInboundHandlerAdapter(exceptionTranslator, requestDispatcher);
        accountController.init();
    }
}
