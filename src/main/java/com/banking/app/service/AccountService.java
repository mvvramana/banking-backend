package com.banking.app.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banking.app.entity.Account;
import com.banking.app.entity.Transaction;
import com.banking.app.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
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
    
    //transfer money
    @Transactional
    public String transferMoney(String fromAccount, String toAccount, Double amount) {
        Account sender = accountRepository
                .findByAccountNumber(fromAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));
        Account receiver = accountRepository
                .findByAccountNumber(toAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found"));
        if(sender.getBalance() < amount){
            throw new RuntimeException("Insufficient Balance");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction debitTransaction = new Transaction();
        debitTransaction.setAccountNumber(fromAccount);
        debitTransaction.setAmount(amount);
        debitTransaction.setType("TRANSFER_DEBIT");
        debitTransaction.setTransactionTime(java.time.LocalDateTime.now());

        Transaction creditTransaction = new Transaction();
        creditTransaction.setAccountNumber(toAccount);
        creditTransaction.setAmount(amount);
        creditTransaction.setType("TRANSFER_CREDIT");
        creditTransaction.setTransactionTime(java.time.LocalDateTime.now());

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        return "Transfer Successful";
    }
}