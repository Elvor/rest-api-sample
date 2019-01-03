package org.elvor.sample.banking.service;

import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.service.vo.TransferInfo;

import java.util.List;
import java.util.Optional;

/**
 * Service for operations on {@link Account}.
 */
public interface AccountService {

    Account create(Account account);

    TransferInfo transfer(TransferInfo transferInfo);

    List<Account> findAll();

    Optional<Account> find(final long id);

    void deleteAll(List<Long> id);

    void delete(final long id);
}
