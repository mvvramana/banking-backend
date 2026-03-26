package com.banking.app.dto;

public class AccountUpdateDTO {

	private String accountHolderName;
	private String mobileNumber;
	private String address;
	private String status;

	public AccountUpdateDTO() {
	}

	public AccountUpdateDTO(String accountHolderName, String mobileNumber, String address, String status) {
		super();
		this.accountHolderName = accountHolderName;
		this.mobileNumber = mobileNumber;
		this.address = address;
		this.status = status;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}