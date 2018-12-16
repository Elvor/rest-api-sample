package org.elvor.sample.banking.repository;

import org.elvor.sample.banking.model.Account;

/**
 * A repository for {@link Account}.
 */
public interface AccountRepository {

    Account getOne(Long id);

    Account save(Account account);
}
