package org.elvor.sample.banking.repository;


import org.elvor.sample.banking.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A simple in-memory implementation backed by concurrent map.
 */
public class AccountRepositoryMapImpl implements AccountRepository {

    private Long idCount = 0L;

    private ConcurrentHashMap<Long, Account> map = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> getOne(final long id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Account save(final Account account) {
        if (account.getId() == null) {
            account.setId(++idCount);
        } else {
            map.computeIfAbsent(
                    account.getId(),
                    o -> {
                        throw new RuntimeException(String.format(
                                "Object with id %d doesn't present in datasource",
                                account.getId()
                        ));
                    });
        }
        map.put(account.getId(), account);
        return account;
    }

    @Override
    public List<Account> getAll(final List<Long> ids) {
        return ids.stream()
                .map(id -> map.get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public void delete(final List<Long> ids) {
        for (Long id : ids) {
            map.remove(id);
        }
    }

    @Override
    public void delete(final long id) {
        map.remove(id);
    }
}
