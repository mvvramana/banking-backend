package com.banking.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.app.dto.RegisterRequest;
import com.banking.app.entity.Account;
import com.banking.app.entity.User;
import com.banking.app.exception.DuplicateResourceException;
import com.banking.app.exception.InvalidCredentialsException;
import com.banking.app.exception.ResourceNotFoundException;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AccountRepository accountRepository;

	public User login(String email, String password) {
		log.info("Validating user login for email: {}", email);

		User user = userRepository.findByEmail(email).orElseThrow(() -> {
			log.error("User not found with email: {}", email);
			return new ResourceNotFoundException("Invalid email or password");
		});

		// Check password
		if (!passwordEncoder.matches(password, user.getPassword())) {
			log.error("Invalid password for email: {}", email);
			throw new InvalidCredentialsException("Invalid password");
		}

		log.info("User login successful: {}", email);
		return user;
	}

	@Transactional
	public void registerUser(RegisterRequest request) {
		log.info("Registering user with email: {}", request.getEmail());

		// Check if email already exists
		if (userRepository.existsByEmail(request.getEmail())) {
			log.error("Email already exists: {}", request.getEmail());
			throw new DuplicateResourceException("Email already exists");
		}

		// Check if phone number already exists in User table
		if (userRepository.existsByPhone(request.getPhone())) {
			log.error("Phone number already exists: {}", request.getPhone());
			throw new DuplicateResourceException("Registration failed: Phone number already registered");
		}

		// Check if an account exists with the same mobile number
		Optional<Account> optionalAccount = accountRepository.findByMobileNumber(request.getPhone());
		if (optionalAccount.isEmpty()) {
			log.error("No account found with mobile number: {}", request.getPhone());
			throw new IllegalArgumentException("Registration failed: No bank account exists with this mobile number.");
		}

		// Convert DTO → Entity
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("ROLE_USER");

		// Save user
		User savedUser = userRepository.save(user);
		log.info("User registered successfully: {}", request.getEmail());

		// Link user to account
		Account account = optionalAccount.get();
		account.setUser(savedUser);
		accountRepository.save(account);

		log.info("Linked account {} to user {}", account.getAccountNumber(), savedUser.getEmail());
	}
}