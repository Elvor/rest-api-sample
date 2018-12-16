package org.elvor.sample.banking.service;

import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.vo.TransferInfo;

public interface AccountService {

    Account create(Account account);

    TransferInfo transfer(TransferInfo transferInfo);
}
