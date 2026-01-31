package com.activitytracker.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.activitytracker.dto.LoginRequestDto;
import com.activitytracker.dto.RegisterRequestDto;
import com.activitytracker.entity.User;
import com.activitytracker.repo.UserRepository;
import com.activitytracker.securityconfig.CustomUserDetailsService;
import com.activitytracker.securityconfig.JwtUtil;
import com.activitytracker.service.UserService;

import jakarta.validation.Valid;

@RestController
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
	UserRepository userRepository;
	
	
	@Autowired
	UserService service;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			// fetch the authenticated user details
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			String token = jwtUtil.generateToken(userDetails.getUsername());

			// Include email and role in response
			Map<String, Object> response = new HashMap<>();
			response.put("token", token);
			response.put("email", userDetails.getUsername());
			response.put("role", userDetails.getAuthorities().stream().findFirst().get().getAuthority());

			return ResponseEntity.ok(response);

		} catch (BadCredentialsException ex) {
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Invalid email or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
	}

	
	//USING THIS when user change token in localstorage on ui this api hits in context file to validate
	@GetMapping("/auth/validate-token")
	public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
		try {
			String token = authHeader.replace("Bearer ", "");
			String username = jwtUtil.extractUsername(token);

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtUtil.validateToken(token, userDetails)) {
				Map<String, Object> res = new HashMap<>();
				res.put("email", userDetails.getUsername());
				res.put("role", userDetails.getAuthorities().stream().findFirst().get().getAuthority());
				return ResponseEntity.ok(res);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
	}
	
	

	// api for register user
	@PostMapping("/api/admin/users")
	public ResponseEntity<?> registerUser(
	        @Valid @RequestBody RegisterRequestDto request,
	        BindingResult bindingResult) {

	    // validation using dependency
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = new HashMap<>();
	        bindingResult.getFieldErrors()
	                .forEach(err -> errors.put("error", err.getDefaultMessage()));
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	    }
	    
	    try {
	        Map<String, Object> response = service.registerUser(request);
	        return ResponseEntity.status(HttpStatus.CREATED).body(response);

	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("error", e.getMessage()));
	    }
	}

}
