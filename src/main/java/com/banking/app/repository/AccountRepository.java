package com.banking.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.banking.app.entity.Account;
import com.banking.app.entity.User;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByAccountNumber(String accountNumber);

	boolean existsByAadhaarNumber(String aadhaarNumber);

	boolean existsByPanNumber(String panNumber);

	Page<Account> findAll(Pageable pageable);

	@Query("SELECT SUM(a.balance) FROM Account a")
	Double getTotalBalance();

	@Query("SELECT COUNT(t) FROM Transaction t")
	long countTransactions();

	List<Account> findByAccountNumberContainingIgnoreCase(String keyword);

	long countByStatus(String status);

	Optional<Account> findByMobileNumber(String mobileNumber);

	Optional<Account> findByUser(User user);

	boolean existsByMobileNumber(String mobileNumber);
}