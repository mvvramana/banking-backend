package com.banking.app.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AccountDeletionException.class)
	public ResponseEntity<Map<String, Object>> handleDeleteException(AccountDeletionException ex) {
		Map<String, Object> error = new HashMap<>();
		error.put("message", ex.getMessage());
		error.put("status", 400);
		error.put("timestamp", LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	// Duplicate
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
		Map<String, Object> error = new HashMap<>();
		error.put("message", ex.getMessage());
		error.put("status", 409);
		error.put("timestamp", LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	// Validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String fieldName = error.getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	// Generic
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneric(Exception ex) {
		log.error("Internal error: ", ex);
		return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Map<String, Object>> handleInvalid(InvalidRequestException ex) {

		Map<String, Object> error = new HashMap<>();
		error.put("message", ex.getMessage());
		error.put("status", HttpStatus.BAD_REQUEST.value());
		error.put("timestamp", LocalDateTime.now());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
		Map<String, Object> error = new HashMap<>();
		error.put("message", ex.getMessage());
		error.put("status", 401);
		error.put("timestamp", LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {

		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage());
		error.put("status", "403");

		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}
}