package com.banking.app.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.app.dto.AccountResponseDTO;
import com.banking.app.entity.Account;
import com.banking.app.entity.Transaction;
import com.banking.app.entity.User;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;
import com.banking.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserDashboardController {

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final AccountRepository accountRepository;

	@Autowired
	private final TransactionRepository transactionRepository;

	/** -------------------- DASHBOARD INFO -------------------- **/
	@GetMapping("/dashboard")
	public ResponseEntity<Map<String, Object>> getDashboardInfo(Principal principal) {
		String email = principal.getName();
		log.info("Fetching dashboard info for user: {}", email);

		User user = getUserByEmail(email);
		Account account = getAccountByUser(user);

		Map<String, Object> response = new HashMap<>();
		response.put("accountNumber", account.getAccountNumber());
		response.put("accountHolderName", account.getAccountHolderName());
		response.put("balance", account.getBalance());

		log.info("Dashboard info prepared for user: {}", email);
		return ResponseEntity.ok(response);
	}

	/** -------------------- FULL ACCOUNT DETAILS -------------------- **/
	@GetMapping("/account")
	public AccountResponseDTO getAccountDetails(Principal principal) {
		String email = principal.getName();
		log.info("Fetching full account details for user: {}", email);

		// Fetch user and account
		User user = getUserByEmail(email);
		Account account = getAccountByUser(user);

		// Map to DTO

		log.info("Full account details returned for user: {}", email);
		return mapToDTO(account);
	}

	private AccountResponseDTO mapToDTO(Account acc) {
		AccountResponseDTO res = new AccountResponseDTO();
		res.setId(acc.getId());
		res.setAccountNumber(acc.getAccountNumber());
		res.setAccountHolderName(acc.getAccountHolderName());
		res.setAccountType(acc.getAccountType());
		res.setBalance(acc.getBalance());
		res.setMobileNumber(acc.getMobileNumber());
		res.setAddress(acc.getAddress());
		res.setAadhaarNumber(acc.getAadhaarNumber());
		res.setPanNumber(acc.getPanNumber());
		res.setStatus(acc.getStatus());
		return res;
	}

	/** -------------------- TRANSACTION HISTORY -------------------- **/
	@GetMapping("/transactions")
	public ResponseEntity<List<Transaction>> getTransactions(Principal principal) {
		String email = principal.getName();
		log.info("Fetching transactions for user: {}", email);

		User user = getUserByEmail(email);
		Account account = getAccountByUser(user);

		List<Transaction> transactions = transactionRepository.findByAccountNumber(account.getAccountNumber());
		log.info("Transactions returned for user: {}, total: {}", email, transactions.size());

		return ResponseEntity.ok(transactions);
	}

	/** -------------------- PRIVATE HELPERS -------------------- **/
	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> {
			log.error("User not found for email: {}", email);
			return new RuntimeException("User not found for email: " + email);
		});
	}

	private Account getAccountByUser(User user) {
		return accountRepository.findByUser(user).orElseThrow(() -> {
			log.error("Account not found for user: {}", user.getEmail());
			return new RuntimeException("Account not found for user: " + user.getEmail());
		});
	}
}