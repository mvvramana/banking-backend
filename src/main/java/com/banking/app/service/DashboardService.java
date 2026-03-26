package com.banking.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.app.dto.DashboardResponse;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public DashboardResponse getDashboardData() {
		log.info("Collecting dashboard metrics");
		try {
			// Total Accounts
			long totalAccounts = accountRepository.count();

			// Total Balance (Null Safe)
			Double totalBalance = accountRepository.getTotalBalance();
			totalBalance = (totalBalance != null) ? totalBalance : 0.0;

			// Total Transactions
			long totalTransactions = transactionRepository.count();

			// Today's Transactions
			LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
			long todayTransactions = transactionRepository.countByTransactionTimeAfter(startOfDay);

			// Active Accounts
			long activeAccounts = accountRepository.countByStatus("ACTIVE");

			// Blocked Accounts
			long blockedAccounts = accountRepository.countByStatus("BLOCKED");

			// Closed Accounts
			long closedAccounts = accountRepository.countByStatus("CLOSED");

			log.info("Dashboard data fetched successfully");
			return new DashboardResponse(totalAccounts, totalBalance, totalTransactions, activeAccounts,
					todayTransactions, blockedAccounts, closedAccounts);
		} catch (Exception ex) {
			log.error("Error while fetching dashboard data", ex);
			throw new RuntimeException("Failed to fetch dashboard data");
		}
	}

}
