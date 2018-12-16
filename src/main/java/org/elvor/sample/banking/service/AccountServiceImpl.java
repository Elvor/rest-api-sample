package org.elvor.sample.banking.service;

import lombok.RequiredArgsConstructor;
import org.elvor.sample.banking.exception.ConflictException;
import org.elvor.sample.banking.exception.NotFoundException;
import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.repository.AccountRepository;
import org.elvor.sample.banking.vo.TransferInfo;

@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account create(final Account account) {
        final Account existing = accountRepository.getOne(account.getId());
        if (existing != null) {
            throw new ConflictException(String.format("Account with id %s already exists", account.getId()));
        }
        return accountRepository.save(account);
    }

    @Override
    public TransferInfo transfer(final TransferInfo transferInfo) {
        final Account from = getAccountSafe(transferInfo.getFrom());
        final Account to = getAccountSafe(transferInfo.getTo());
        if (from.getMoney() < transferInfo.getAmount()) {
            throw new ConflictException(String.format("Not enough money on account %s", from.getId()));
        }
        //TODO synchronise!
        from.setMoney(from.getMoney() - transferInfo.getAmount());
        to.setMoney(to.getMoney() + transferInfo.getAmount());
        return transferInfo;
    }

    private Account getAccountSafe(final Long id) {
        final Account account = accountRepository.getOne(id);
        if (account == null) {
            throw new NotFoundException(String.format("Account with id %s doesn't exist", id));
        }
        return account;
    }
}
