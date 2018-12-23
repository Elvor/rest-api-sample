package org.elvor.sample.banking.repository;

import org.elvor.sample.banking.model.Account;

import java.util.List;

/**
 * A repository for {@link Account}.
 */
public interface AccountRepository {

    Account getOne(Long id);

    Account save(Account account);

    // TODO add pagination
    List<Account> getAll(List<Long> ids);

    List<Account> getAll();

    void delete(List<Long> ids);
}
