package com.banking.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.banking.app.dto.DashboardResponse;
import com.banking.app.service.DashboardService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dashboard")
@Slf4j
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/details")
	public ResponseEntity<DashboardResponse> getDashboard() {

		log.info("Fetching dashboard data");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.info("Logged in user: {}", auth.getName());
		log.info("Authorities: {}", auth.getAuthorities());

		DashboardResponse response = dashboardService.getDashboardData();
		return ResponseEntity.ok(response);
	}
}
