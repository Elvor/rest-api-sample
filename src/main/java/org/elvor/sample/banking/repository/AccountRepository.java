package org.elvor.sample.banking.repository;

import org.elvor.sample.banking.model.Account;

import java.util.List;
import java.util.Optional;

/**
 * A repository for {@link Account}.
 */
public interface AccountRepository {

    Optional<Account> getOne(long id);

    Account save(Account account);

    // TODO add pagination
    List<Account> getAll(List<Long> ids);

    List<Account> getAll();

    void delete(List<Long> ids);

    void delete(long id);
}
