package com.banking.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banking.app.dto.TransactionResponseDTO;
import com.banking.app.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class TransactionController {

	@Autowired
	private TransactionService service;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/transactions/{accountNumber}")
	public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(@PathVariable String accountNumber,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		log.info("Fetching transactions for account: {}, page: {}, size: {}", accountNumber, page, size);
		Page<TransactionResponseDTO> transactions = service.getTransactions(accountNumber, page, size);
		return ResponseEntity.ok(transactions);
	}
}