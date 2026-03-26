package com.banking.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateAccountRequestDTO {

	@NotBlank(message = "Name is required")
	private String accountHolderName;

	@NotBlank(message = "Account type is required")
	private String accountType;

	@NotNull(message = "Balance is required")
	private Double balance;

	@Pattern(regexp = "^[0-9]{10}$", message = "Invalid mobile number")
	private String mobileNumber;

	@NotBlank(message = "Address is required")
	private String address;

	@NotBlank(message = "Aadhaar is required")
	private String aadhaarNumber;

	@NotBlank(message = "PAN is required")
	private String panNumber;


	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAadhaarNumber() {
		return aadhaarNumber;
	}

	public void setAadhaarNumber(String aadhaarNumber) {
		this.aadhaarNumber = aadhaarNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	

}