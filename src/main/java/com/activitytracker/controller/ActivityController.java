package com.activitytracker.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.activitytracker.dto.UserProgramResponse;
import com.activitytracker.entity.ActivityType;
import com.activitytracker.entity.Program;
import com.activitytracker.entity.User;
import com.activitytracker.entity.UserProgram;
import com.activitytracker.entity.WorkContext;
import com.activitytracker.repo.UserProgramRepository;
import com.activitytracker.repo.UserRepository;
import com.activitytracker.securityconfig.JwtUtil;

@RestController
public class ActivityController {
	
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserProgramRepository programRepository;
	
	@GetMapping("/activityTypeData")
    public List<String> getActivityTypes() {
        // converting enum data to list of strings  
        return Arrays.stream(ActivityType.values())
                     .map(Enum::name)
                     .toList();
    }
	
	@GetMapping("/workContextData")
    public List<String> workContextData() {
        // converting enum data to list of strings  
        return Arrays.stream(WorkContext.values())
                     .map(Enum::name)
                     .toList();
    }

	 @GetMapping("/user-specific-programs")
	    public ResponseEntity<List<UserProgramResponse>> getProgramsForUser(
	            @RequestHeader("Authorization") String authHeader) {

	        try {
	            //extractin token
	            String token = authHeader.substring(7);

	            // Get username from token
	            String username = jwtUtil.extractUsername(token);

	            // Find user by username to get userId
	            User user = userRepository.findByEmail(username)
	                    .orElseThrow(() -> new RuntimeException("User not found"));

	            Long userId = user.getId();

	            // Fetch programs only for this user
	            List<UserProgram> programs = programRepository.findProgramsByUserId(userId);
	            
	            //custome response return user id program name and program id so i can use in user id in ui and pass in next call
	            List<UserProgramResponse> response = programs.stream()
	                    .map(up -> new UserProgramResponse(
	                            userId,
	                            up.getProgram().getId(),
	                            up.getProgram().getName()
	                    ))
	                    .toList();
	            return ResponseEntity.ok(response);

	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        }
	    }

}
