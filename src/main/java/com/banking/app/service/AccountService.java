package com.banking.app.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.app.entity.Account;
import com.banking.app.entity.Transaction;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    //create account
    public Account createAccount(Account account) {
        account.setAccountNumber(generateAccountNumber());
        return accountRepository.save(account);
    }

    //generate token
    private String generateAccountNumber() {
        return "AC" + System.currentTimeMillis();
    }
    
    //deposit
    public Account deposit(String accountNumber, Double amount) {
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountNumber(accountNumber);
        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");
        transaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(transaction);
        return account;
    }
    
    //withdraw
    public Account withdraw(String accountNumber, Double amount) {
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if(account.getBalance() < amount){
            throw new RuntimeException("Insufficient Balance");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountNumber(accountNumber);
        transaction.setAmount(amount);
        transaction.setType("WITHDRAW");
        transaction.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(transaction);
        return account;
    }
}