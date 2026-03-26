package com.banking.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banking.app.dto.AccountListDTO;
import com.banking.app.dto.AccountResponseDTO;
import com.banking.app.dto.AccountUpdateDTO;
import com.banking.app.dto.CreateAccountRequestDTO;
import com.banking.app.dto.DepositRequestDTO;
import com.banking.app.dto.TransferRequestDTO;
import com.banking.app.service.AccountService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/accounts")
@Slf4j
public class AccountController {

	@Autowired
	private AccountService accountService;

	// Admin only
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody @Valid CreateAccountRequestDTO request) {
		log.info("Received request to create account for: {}", request.getAccountHolderName());
		AccountResponseDTO response = accountService.createAccount(request);
		log.info("Account created successfully with number: {}", response.getAccountNumber());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Admin can view all accounts
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/get")
	public ResponseEntity<Page<AccountListDTO>> getAllAccounts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {

		log.info("Fetching accounts - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
		Page<AccountListDTO> accounts = accountService.getAllAccounts(page, size, sortBy, sortDir);
		return ResponseEntity.ok(accounts);
	}

	// USER or ADMIN can get account details
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/{id}")
	public ResponseEntity<AccountResponseDTO> getAccountDetails(@PathVariable Long id) {
		log.info("Fetching account details for ID: {}", id);
		AccountResponseDTO account = accountService.getAccountById(id);
		return ResponseEntity.ok(account);
	}

	// USER or ADMIN can deposit
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@PostMapping("/deposit")
	public ResponseEntity<AccountResponseDTO> deposit(@RequestBody @Valid DepositRequestDTO request) {
		log.info("Deposit request received for account: {}", request.getAccountNumber());
		AccountResponseDTO response = accountService.deposit(request.getAccountNumber(), request.getAmount());
		log.info("Deposit successful for account: {}", request.getAccountNumber());
		return ResponseEntity.ok(response);
	}

	// USER or ADMIN can withdraw
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@PostMapping("/withdraw")
	public ResponseEntity<AccountResponseDTO> withdraw(@RequestBody @Valid DepositRequestDTO request) {
		log.info("Withdraw request for account: {}", request.getAccountNumber());
		AccountResponseDTO response = accountService.withdraw(request.getAccountNumber(), request.getAmount());
		log.info("Withdraw successful for account: {}", request.getAccountNumber());
		return ResponseEntity.ok(response);
	}

	// USER or ADMIN can transfer
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@PostMapping("/transfer")
	public ResponseEntity<Map<String, String>> transferMoney(@RequestBody @Valid TransferRequestDTO request) {
		log.info("Transfer request from {} to {}", request.getFromAccount(), request.getToAccount());
		accountService.transferMoney(request.getFromAccount(), request.getToAccount(), request.getAmount());
		Map<String, String> response = new HashMap<>();
		response.put("message", "Transfer Successful");
		return ResponseEntity.ok(response);
	}

	// Admin only
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/update/{id}")
	public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id, @RequestBody AccountUpdateDTO dto) {
		log.info("Updating account with ID: {}", id);
		AccountResponseDTO updatedAccount = accountService.updateAccount(id, dto);
		return ResponseEntity.ok(updatedAccount);
	}

	// Admin only
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Map<String, String>> deleteAccount(@PathVariable Long id) {
		log.info("Request received to delete account with ID: {}", id);
		accountService.deleteAccount(id);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Account deleted successfully");
		return ResponseEntity.ok(response);
	}

	// Admin only
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/search")
	public ResponseEntity<List<AccountListDTO>> searchAccounts(@RequestParam String keyword) {
		log.info("Searching accounts with keyword: {}", keyword);
		List<AccountListDTO> accounts = accountService.searchAccounts(keyword);
		return ResponseEntity.ok(accounts);
	}
}