package com.banking.app.dto;

public class DashboardResponse {

	private long totalAccounts;
	private double totalBalance;
	private long totalTransactions;
	private long activeAccounts;
	private long todayTransactions;
	private long blockedAccounts;
	private long closedAccounts;

	public DashboardResponse() {
		super();
	}

	public DashboardResponse(long totalAccounts, double totalBalance, long totalTransactions, long activeAccounts,
			long todayTransactions, long blockedAccounts, long closedAccounts) {
		super();
		this.totalAccounts = totalAccounts;
		this.totalBalance = totalBalance;
		this.totalTransactions = totalTransactions;
		this.activeAccounts = activeAccounts;
		this.todayTransactions = todayTransactions;
		this.blockedAccounts = blockedAccounts;
		this.closedAccounts = closedAccounts;
	}

	public long getTotalAccounts() {
		return totalAccounts;
	}

	public void setTotalAccounts(long totalAccounts) {
		this.totalAccounts = totalAccounts;
	}

	public double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(double totalBalance) {
		this.totalBalance = totalBalance;
	}

	public long getTotalTransactions() {
		return totalTransactions;
	}

	public void setTotalTransactions(long totalTransactions) {
		this.totalTransactions = totalTransactions;
	}

	public long getActiveAccounts() {
		return activeAccounts;
	}

	public void setActiveAccounts(long activeAccounts) {
		this.activeAccounts = activeAccounts;
	}

	public long getTodayTransactions() {
		return todayTransactions;
	}

	public void setTodayTransactions(long todayTransactions) {
		this.todayTransactions = todayTransactions;
	}

	public long getBlockedAccounts() {
		return blockedAccounts;
	}

	public void setBlockedAccounts(long blockedAccounts) {
		this.blockedAccounts = blockedAccounts;
	}

	public long getClosedAccounts() {
		return closedAccounts;
	}

	public void setClosedAccounts(long closedAccounts) {
		this.closedAccounts = closedAccounts;
	}

}