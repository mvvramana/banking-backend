package com.banking.app.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.banking.app.entity.User;
import com.banking.app.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	@Autowired
	private UserRepository userRepository;

	public String generateToken(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) {
			throw new RuntimeException("User not found with email: " + email);
		}
		User user = optionalUser.get();
		// Remove ROLE_ prefix before storing in token
		String cleanRole = user.getRole().replace("ROLE_", ""); // "ADMIN" or "USER"

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", cleanRole);

		return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
				.signWith(key).compact();
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return (String) extractClaims(token).get("role"); // "ADMIN" or "USER"
	}
}