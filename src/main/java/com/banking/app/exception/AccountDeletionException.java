package com.banking.app.exception;

public class AccountDeletionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccountDeletionException(String message) {
		super(message);
	}
}
