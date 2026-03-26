package com.banking.app.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		// Skip authentication for login and register
		return path.equals("/api/users/login") || path.equals("/api/users/register");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		log.info("JwtFilter running for path: {}", request.getServletPath());

		try {
			String header = request.getHeader("Authorization");

			if (header != null && header.startsWith("Bearer ")) {

				String token = header.substring(7);

				String email = jwtUtil.extractEmail(token);
				String role = jwtUtil.extractRole(token);

				log.info("JWT Email: {}", email);
				log.info("JWT Role: {}", role);

				// IMPORTANT CHECK
				if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {

					List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

					log.info("GrantedAuthorities: {}", authorities);

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
							authorities);

					// Attach request details
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					// SET AUTHENTICATION
					SecurityContextHolder.getContext().setAuthentication(auth);

					log.info("✅ Authentication set for user: {}", email);
				}
			}

		} catch (Exception e) {
			log.error("❌ JWT Filter error: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}