package com.banking.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.app.dto.LoginRequest;
import com.banking.app.dto.LoginResponse;
import com.banking.app.dto.RegisterRequest;
import com.banking.app.security.JwtUtil;
import com.banking.app.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		log.info("Login attempt for email: {}", request.getEmail());
		userService.login(request.getEmail(), request.getPassword());
		String token = jwtUtil.generateToken(request.getEmail());
		LoginResponse response = new LoginResponse(token, "Login Successful ✅");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest request) {
		log.info("Register request for email: {}", request.getEmail());
		userService.registerUser(request);
		Map<String, String> response = new HashMap<>();
		response.put("message", "User registered successfully ✅");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}