package com.banking.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.banking.app.dto.AccountListDTO;
import com.banking.app.dto.AccountResponseDTO;
import com.banking.app.dto.AccountUpdateDTO;
import com.banking.app.dto.CreateAccountRequestDTO;
import com.banking.app.entity.Account;
import com.banking.app.entity.Transaction;
import com.banking.app.entity.User;
import com.banking.app.exception.AccountDeletionException;
import com.banking.app.exception.DuplicateResourceException;
import com.banking.app.exception.InvalidRequestException;
import com.banking.app.exception.ResourceNotFoundException;
import com.banking.app.exception.UnauthorizedException;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;
import com.banking.app.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserRepository userRepository;

	// create account
	public AccountResponseDTO createAccount(CreateAccountRequestDTO request) {
		log.info("Creating account for mobile: {}", request.getMobileNumber());

		// ✅ Check duplicate Aadhaar
		if (accountRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
			throw new DuplicateResourceException("Aadhaar already exists");
		}

		// ✅ Check duplicate PAN
		if (accountRepository.existsByPanNumber(request.getPanNumber())) {
			throw new DuplicateResourceException("PAN already exists");
		}

		// ✅ NEW: Check duplicate Mobile Number
		if (accountRepository.existsByMobileNumber(request.getMobileNumber())) {
			throw new DuplicateResourceException("Mobile number already linked to an account");
		}

		// ✅ Create entity
		Account account = new Account();
		account.setAccountHolderName(request.getAccountHolderName());
		account.setAccountType(request.getAccountType());
		account.setBalance(request.getBalance());
		account.setMobileNumber(request.getMobileNumber());
		account.setAddress(request.getAddress());
		account.setAadhaarNumber(request.getAadhaarNumber());
		account.setPanNumber(request.getPanNumber());
		account.setAccountNumber(generateAccountNumber());
		account.setStatus("ACTIVE");

		Account savedAccount = accountRepository.save(account);
		log.info("Account saved in DB with ID: {}", savedAccount.getId());

		return mapToDTO(savedAccount);
	}

	// Mapper method
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

	// Account number generator
	private String generateAccountNumber() {
		return "ACC" + System.currentTimeMillis();
	}

	// Pagination method
	public Page<AccountListDTO> getAllAccounts(int page, int size, String sortBy, String sortDir) {
		log.info("Fetching all accounts");
		// Validate page & size
		if (page < 0 || size <= 0) {
			throw new InvalidRequestException("Invalid pagination parameters");
		}
		// Sorting logic
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Account> accounts = accountRepository.findAll(pageable);
		// Convert Entity → DTO
		return accounts.map(this::mapToAccountListDTO);
	}

	public AccountResponseDTO getAccountById(Long id) {
		log.info("Fetching account from DB with ID: {}", id);
		Account account = accountRepository.findById(id).orElseThrow(() -> {
			log.error("Account not found with ID: {}", id);
			return new ResourceNotFoundException("Account not found with ID: " + id);
		});
		return mapToDTO(account);
	}

	// deposit
	public AccountResponseDTO deposit(String accountNumber, Double amount) {
		log.info("Deposit started for account: {}", accountNumber);
		validateAmount(amount);
		Account account = getActiveAccount(accountNumber);
		account.setBalance(account.getBalance() + amount);
		Account updatedAccount = accountRepository.save(account);
		log.info("Deposit successful. New balance: {}", updatedAccount.getBalance());
		saveTransaction(accountNumber, amount, "DEPOSIT");
		return mapToDTO(updatedAccount);
	}

	// withdraw
	public AccountResponseDTO withdraw(String accountNumber, Double amount) {

		log.info("Withdraw started for account: {}", accountNumber);

		validateAmount(amount);

		Account account = getActiveAccount(accountNumber);

		// ✅ Get logged-in user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		User currentUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// ✅ Check role
		boolean isAdmin = currentUser.getRole().equals("ROLE_ADMIN");

		// ✅ If USER → allow only own account
		if (!isAdmin) {
			if (account.getUser() == null || !account.getUser().getId().equals(currentUser.getId())) {

				log.error("Unauthorized withdraw attempt by user: {}", email);
				throw new UnauthorizedException("You can withdraw only from your account");
			}
		}

		// ✅ Balance check
		if (account.getBalance() < amount) {
			log.error("Insufficient balance. Available: {}, Requested: {}", account.getBalance(), amount);
			throw new InvalidRequestException("Insufficient balance");
		}

		// ✅ Perform withdraw
		account.setBalance(account.getBalance() - amount);
		Account updatedAccount = accountRepository.save(account);

		log.info("Withdraw successful. Remaining balance: {}", updatedAccount.getBalance());

		saveTransaction(accountNumber, amount, "WITHDRAW");

		return mapToDTO(updatedAccount);
	}

	// transfer money
	@Transactional
	public void transferMoney(String fromAccount, String toAccount, Double amount) {

		log.info("Transfer initiated from {} to {}", fromAccount, toAccount);

		// ✅ Validate input
		if (fromAccount == null || toAccount == null || fromAccount.isEmpty() || toAccount.isEmpty()) {
			throw new InvalidRequestException("Account numbers must not be empty");
		}

		if (fromAccount.equals(toAccount)) {
			throw new InvalidRequestException("Cannot transfer to same account");
		}

		if (amount == null || amount <= 0) {
			throw new InvalidRequestException("Amount must be greater than zero");
		}

		// ✅ Fetch sender
		Account sender = accountRepository.findByAccountNumber(fromAccount).orElseThrow(() -> {
			log.error("Sender account not found: {}", fromAccount);
			return new ResourceNotFoundException("Sender account not found");
		});

		// 🔥 NEW: SECURITY CHECK
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		User currentUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		boolean isAdmin = currentUser.getRole().equals("ROLE_ADMIN");

		if (!isAdmin) {
			if (sender.getUser() == null || !sender.getUser().getId().equals(currentUser.getId())) {

				log.error("Unauthorized transfer attempt by user: {}", email);
				throw new UnauthorizedException("You can transfer only from your account");
			}
		}

		// ✅ Fetch receiver
		Account receiver = accountRepository.findByAccountNumber(toAccount).orElseThrow(() -> {
			log.error("Receiver account not found: {}", toAccount);
			return new ResourceNotFoundException("Receiver account not found");
		});

		// ✅ Check account status
		if (!"ACTIVE".equalsIgnoreCase(sender.getStatus())) {
			throw new InvalidRequestException("Sender account is not active");
		}

		if (!"ACTIVE".equalsIgnoreCase(receiver.getStatus())) {
			throw new InvalidRequestException("Receiver account is not active");
		}

		// ✅ Check balance
		if (sender.getBalance() < amount) {
			log.error("Insufficient balance. Available: {}, Requested: {}", sender.getBalance(), amount);
			throw new InvalidRequestException("Insufficient balance");
		}

		// ✅ Perform transfer
		sender.setBalance(sender.getBalance() - amount);
		receiver.setBalance(receiver.getBalance() + amount);

		accountRepository.save(sender);
		accountRepository.save(receiver);

		log.info("Transfer successful. Sender balance: {}, Receiver balance: {}", sender.getBalance(),
				receiver.getBalance());

		// ✅ Save transactions
		saveTransaction(fromAccount, amount, "TRANSFER_DEBIT");
		saveTransaction(toAccount, amount, "TRANSFER_CREDIT");
	}

	public AccountResponseDTO updateAccount(Long id, AccountUpdateDTO dto) {
		log.info("Fetching account for update. ID: {}", id);

		// Fetch account
		Account account = accountRepository.findById(id).orElseThrow(() -> {
			log.error("Account not found with ID: {}", id);
			return new ResourceNotFoundException("Account not found with ID: " + id);
		});

		// Update allowed fields
		if (dto.getAccountHolderName() != null && !dto.getAccountHolderName().isBlank()) {
			account.setAccountHolderName(dto.getAccountHolderName());
		}

		if (dto.getMobileNumber() != null && !dto.getMobileNumber().isBlank()) {
			account.setMobileNumber(dto.getMobileNumber());

			// Also update the corresponding user's phone
			User user = account.getUser();
			if (user != null) {
				user.setPhone(dto.getMobileNumber());
				log.info("Updated user's phone to {}", dto.getMobileNumber());
				// Optional: save user if necessary (usually JPA handles via cascade)
			}
		}

		if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
			account.setAddress(dto.getAddress());
		}

		if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
			account.setStatus(dto.getStatus());
		}

		// Save account (and cascade should save user phone as well if properly
		// configured)
		Account updatedAccount = accountRepository.save(account);

		log.info("Account updated successfully. ID: {}", id);
		return mapToDTO(updatedAccount);
	}

	public void deleteAccount(Long id) {
		log.info("Attempting to delete account with ID: {}", id);
		Account account = accountRepository.findById(id).orElseThrow(() -> {
			log.error("Account not found with ID: {}", id);
			return new ResourceNotFoundException("Account not found with ID: " + id);
		});
		// Business validation
		if (account.getBalance() > 0) {
			log.error("Cannot delete account with balance: {}", account.getBalance());
			throw new AccountDeletionException("Account cannot be deleted. Balance must be zero.");
		}
		accountRepository.delete(account);
		log.info("Account deleted successfully with ID: {}", id);
	}

	// Get account by account number
	public AccountResponseDTO getAccountByNumber(String accountNumber) {
		log.info("Fetching account from DB: {}", accountNumber);
		// Validate input
		if (accountNumber == null || accountNumber.trim().isEmpty()) {
			throw new InvalidRequestException("Account number cannot be empty");
		}
		Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> {
			log.error("Account not found: {}", accountNumber);
			return new ResourceNotFoundException("Account not found");
		});
		// Optional: check account status
		if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
			throw new InvalidRequestException("Account is not active");
		}
		return mapToDTO(account);
	}

	// Validate amount
	private void validateAmount(Double amount) {
		if (amount == null || amount <= 0) {
			throw new InvalidRequestException("Amount must be greater than zero");
		}
	}

	// Fetch + validate account
	private Account getActiveAccount(String accountNumber) {
		Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> {
			log.error("Account not found: {}", accountNumber);
			return new ResourceNotFoundException("Account not found");
		});

		if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
			throw new InvalidRequestException("Account is not active");
		}
		return account;
	}

	// Save transaction
	private void saveTransaction(String accountNumber, Double amount, String type) {
		Transaction txn = new Transaction();
		txn.setAccountNumber(accountNumber);
		txn.setAmount(amount);
		txn.setType(type);
		txn.setTransactionTime(LocalDateTime.now());
		transactionRepository.save(txn);
		log.info("{} transaction saved for account: {}", type, accountNumber);
	}

	private AccountListDTO mapToAccountListDTO(Account account) {
		AccountListDTO dto = new AccountListDTO();
		dto.setId(account.getId());
		dto.setAccountNumber(account.getAccountNumber());
		dto.setAccountHolderName(account.getAccountHolderName());
		dto.setAccountType(account.getAccountType());
		dto.setBalance(account.getBalance());
		dto.setStatus(account.getStatus());
		return dto;
	}

	public List<AccountListDTO> searchAccounts(String keyword) {
		log.info("Filtering accounts by keyword: {}", keyword);
		List<Account> accounts = accountRepository.findByAccountNumberContainingIgnoreCase(keyword);
		if (accounts.isEmpty()) {
			log.warn("No accounts found for keyword: {}", keyword);
		}
		return accounts.stream().map(this::mapToDTOList).toList();
	}

	public AccountListDTO mapToDTOList(Account acc) {
		return new AccountListDTO(acc.getId(), acc.getAccountHolderName(), acc.getAccountNumber(), acc.getAccountType(),
				acc.getBalance(), acc.getStatus());
	}
}