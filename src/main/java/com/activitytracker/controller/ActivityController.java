package com.activitytracker.controller;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.activitytracker.dto.ActivityLogRequest;
import com.activitytracker.dto.ActivityLogResponse;
import com.activitytracker.dto.UserProgramResponse;
import com.activitytracker.entity.ActivityLog;
import com.activitytracker.entity.ActivityType;
import com.activitytracker.entity.Program;
import com.activitytracker.entity.User;
import com.activitytracker.entity.UserProgram;
import com.activitytracker.entity.WorkContext;
import com.activitytracker.repo.UserProgramRepository;
import com.activitytracker.repo.UserRepository;
import com.activitytracker.securityconfig.JwtUtil;
import com.activitytracker.service.ActivityLogService;

@RestController
public class ActivityController {
	
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserProgramRepository programRepository;
	
	@Autowired
	private ActivityLogService activityLogService;
	
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
	 
	  @PostMapping("/save-activity")
	    public ResponseEntity<?> saveActivity(
	            @RequestBody ActivityLogRequest request
	    ) {
	        try {
	            activityLogService.saveActivity(request);

	            // Success response
				Map<String, String> response = new HashMap<>();
				response.put("message", "Activity created successfully");
				return ResponseEntity.ok(response);

			} catch (RuntimeException e) {
				//this run due to some validation failed error user not found etc type errro
				Map<String, String> error = new HashMap<>();
				error.put("error", e.getMessage());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
			}
			catch (Exception e) {
	            Map<String, String> error = new HashMap<>();
	            error.put("error", "Something went wrong");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	        }

	    }
	 
	  // getting all actitiity data for admin only
	    @GetMapping("/getall-activity")
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<List<ActivityLogResponse>> getAllActivityLogs() {
	        List<ActivityLogResponse> allActivityData = activityLogService.getAllActivityLogs();
	        return ResponseEntity.ok(allActivityData);
	    }
	    
	 // getting acvtiivty log data user specific by id
	    @GetMapping("/user/{userId}")
	    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	    public ResponseEntity<List<ActivityLog>> getUserActivities(@PathVariable Long userId) {
	        List<ActivityLog> activities = activityLogService.getUserActivities(userId);
	        return ResponseEntity.ok(activities);
	    }

	    @GetMapping("/admin/program-output-count")
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<?> getProgramOutputSummary() {

	        try {
	        	 Map<String, Object> data = activityLogService.getProgramOutput();
	             return ResponseEntity.ok(data);

	        } catch (RuntimeException e) {
	            Map<String, String> error = new HashMap<>();
	            error.put("error", e.getMessage());
	            return ResponseEntity.badRequest().body(error);

	        } catch (Exception e) {
	            Map<String, String> error = new HashMap<>();
	            error.put("error", "Something went wrong");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	        }
	    }
	    
	    @GetMapping("/export-activities")
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<byte[]> exportActivities() {
	        try {
	        	//extracting all data from db
	            List<ActivityLogResponse> activities = activityLogService.getAllActivityLogs();

	            //seeting table header
	            StringBuilder sb = new StringBuilder();
	            sb.append("User ID,User Name,Program Name,Activity Date,Activity Type,Work Context,Output Count,Notes\n");

	            for (ActivityLogResponse a : activities) {
	                sb.append(a.getUserId()).append(",");
	                sb.append('"').append(a.getUserName().replace("\"", "\"\"")).append("\",");
	                sb.append('"').append(a.getProgramName().replace("\"", "\"\"")).append("\",");
	                sb.append(a.getActivityDate()).append(",");
	                sb.append(a.getActivityType()).append(",");
	                sb.append(a.getWorkContext()).append(",");
	                sb.append(a.getOutputCount()).append(",");
	                sb.append('"').append(a.getNotes().replace("\"", "\"\"")).append("\"\n");
	            }

	            byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activity_logs.csv")
	                    .contentType(new MediaType("text", "csv"))
	                    .body(csvBytes);

	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }

	    
}
