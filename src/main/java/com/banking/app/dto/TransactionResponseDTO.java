package com.banking.app.dto;

import java.time.LocalDateTime;

public class TransactionResponseDTO {

	private Long id;
    private String accountNumber;
    private Double amount;
    private String type;
    private LocalDateTime transactionTime;
	public TransactionResponseDTO() {
		super();
	}
	public TransactionResponseDTO(Long id, String accountNumber, Double amount, String type,
			LocalDateTime transactionTime) {
		super();
		this.id = id;
		this.accountNumber = accountNumber;
		this.amount = amount;
		this.type = type;
		this.transactionTime = transactionTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(LocalDateTime transactionTime) {
		this.transactionTime = transactionTime;
	}
	
}