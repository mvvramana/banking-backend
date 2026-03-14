package com.banking.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.banking.app.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByAccountNumber(String accountNumber);

}