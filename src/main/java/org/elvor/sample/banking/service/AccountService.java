package org.elvor.sample.banking.service;

import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.service.vo.TransferInfo;

import java.util.List;

/**
 * Service for operations on {@link Account}.
 */
public interface AccountService {

    Account create(Account account);

    TransferInfo transfer(TransferInfo transferInfo);

    List<Account> findAll(List<Long> ids);

    void deleteAll(List<Long> id);
}
