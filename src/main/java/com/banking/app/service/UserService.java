package com.banking.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.app.entity.User;
import com.banking.app.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public User login(String email, String password) {
	    User user = userRepository.findByEmail(email);
	    if(user == null) {
	        throw new RuntimeException("User not found");
	    }
	    if(!passwordEncoder.matches(password, user.getPassword())) {
	        throw new RuntimeException("Invalid password");
	    }
	    return user;
	}
	
	public User registerUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

}