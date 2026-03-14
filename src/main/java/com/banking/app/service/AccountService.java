package com.banking.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.app.entity.Account;
import com.banking.app.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        account.setAccountNumber(generateAccountNumber());
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        return "AC" + System.currentTimeMillis();
    }
}