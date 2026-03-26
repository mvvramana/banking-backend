package com.banking.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> {
		}).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth

						// Allow CORS preflight
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// Public endpoints
						.requestMatchers("/api/users/login", "/api/users/register").permitAll()

						// ADMIN endpoints
						.requestMatchers("/api/accounts/create", "/api/accounts/update/**", "/api/accounts/delete/**",
								"/api/accounts/search", "/api/dashboard/details", "/api/dashboard/**")
						.hasRole("ADMIN")

						// USER + ADMIN endpoints
						.requestMatchers("/api/accounts/deposit", "/api/accounts/withdraw", "/api/accounts/transfer",
								"/api/accounts/transactions", "/api/accounts/**" // Use /** to match any account id
						).hasAnyRole("USER", "ADMIN")

						// All other requests require authentication
						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}