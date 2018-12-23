package org.elvor.sample.banking.service;

import lombok.RequiredArgsConstructor;
import org.elvor.sample.banking.exception.ConflictException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.exception.ValidationException;
import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.repository.AccountRepository;
import org.elvor.sample.banking.service.vo.TransferInfo;

import java.util.List;

/**
 * The simplest synchronized implementation of {@link AccountService}.
 */
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public synchronized Account create(final Account account) {
        if (account.getId() != null) {
            throw new ValidationException("Creating account must not contain id");
        }
        if (account.getOwnerName() == null) {
            throw new ValidationException("Account owner name is required");
        }
        if (account.getMoney() < 0) {
            throw new ValidationException("Amount of money must be non-negative");
        }
        return accountRepository.save(account);
    }

    //bottleneck
    //TODO write some transaction manager.
    @Override
    public synchronized TransferInfo transfer(final TransferInfo transferInfo) {
        final Account from = getAccountSafe(transferInfo.getFrom());
        final Account to = getAccountSafe(transferInfo.getTo());
        if (from.getMoney() < transferInfo.getAmount()) {
            throw new ConflictException(String.format("Not enough money on account %s", from.getId()));
        }

        from.setMoney(from.getMoney() - transferInfo.getAmount());
        to.setMoney(to.getMoney() + transferInfo.getAmount());
        return transferInfo;
    }

    @Override
    public List<Account> findAll(final List<Long> ids) {
        if (ids.isEmpty()) {
            return accountRepository.getAll();
        }
        return accountRepository.getAll(ids);
    }

    @Override
    public synchronized void deleteAll(final List<Long> ids) {
        accountRepository.delete(ids);
    }

    private Account getAccountSafe(final Long id) {
        final Account account = accountRepository.getOne(id);
        if (account == null) {
            throw new NotFoundException(String.format("Account with id %s doesn't exist", id));
        }
        return account;
    }
}
