package com.banking.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.app.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByAccountNumber(String accountNumber);

	Page<Transaction> findByAccountNumber(String accountNumber, Pageable pageable);

	long countByTransactionTimeAfter(LocalDateTime time);

}