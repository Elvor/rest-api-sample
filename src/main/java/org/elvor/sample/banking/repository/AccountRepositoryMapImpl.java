package org.elvor.sample.banking.repository;


import org.elvor.sample.banking.model.Account;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple in-memory implementation backed by concurrent map.
 */
public class AccountRepositoryMapImpl implements AccountRepository {

    private ConcurrentHashMap<Long, Account> map = new ConcurrentHashMap<>();

    @Override
    public Account getOne(final Long id) {
        return map.get(id);
    }

    @Override
    public Account save(Account account) {
        return map.put(account.getId(), account);
    }
}
