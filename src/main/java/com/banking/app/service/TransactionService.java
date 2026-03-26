package com.banking.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banking.app.dto.TransactionResponseDTO;
import com.banking.app.entity.Transaction;
import com.banking.app.exception.ResourceNotFoundException;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

	@Autowired
	private TransactionRepository repo;

	@Autowired
	private AccountRepository accountRepository;

	public Page<TransactionResponseDTO> getTransactions(String accountNumber, int page, int size) {
		log.info("Fetching transactions for account: {}", accountNumber);
		// Validate account exists (CORRECT WAY)
		accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> {
			log.error("Account not found: {}", accountNumber);
			return new ResourceNotFoundException("Account not found");
		});
		// Pagination + Sorting
		Pageable pageable = PageRequest.of(page, size, Sort.by("transactionTime").descending());
		Page<Transaction> transactions = repo.findByAccountNumber(accountNumber, pageable);
		// Convert Entity → DTO
		return transactions.map(this::mapToDTO);
	}

	// Mapper method
	private TransactionResponseDTO mapToDTO(Transaction txn) {
		TransactionResponseDTO dto = new TransactionResponseDTO();
		dto.setId(txn.getId());
		dto.setAccountNumber(txn.getAccountNumber());
		dto.setAmount(txn.getAmount());
		dto.setType(txn.getType());
		dto.setTransactionTime(txn.getTransactionTime());
		return dto;
	}
}