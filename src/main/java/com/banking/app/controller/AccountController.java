package com.banking.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.banking.app.entity.Account;
import com.banking.app.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }
    
    @PostMapping("/deposit")
    public Account deposit(@RequestParam String accountNumber,
                           @RequestParam Double amount) {
        return accountService.deposit(accountNumber, amount);
    }
    
    @PostMapping("/withdraw")
    public Account withdraw(@RequestParam String accountNumber,
                            @RequestParam Double amount) {
        return accountService.withdraw(accountNumber, amount);
    }
}