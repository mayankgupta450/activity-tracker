package com.activitytracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activitytracker.dto.ActivityLogRequest;
import com.activitytracker.dto.ActivityLogResponse;
import com.activitytracker.entity.ActivityLog;
import com.activitytracker.entity.ActivityType;
import com.activitytracker.entity.Program;
import com.activitytracker.entity.User;
import com.activitytracker.entity.WorkContext;
import com.activitytracker.repo.ActivityLogRepository;
import com.activitytracker.repo.ProgramRepository;
import com.activitytracker.repo.UserRepository;

@Service
public class ActivityLogService {
	
	@Autowired
    private  ActivityLogRepository activityLogRepository;
	
	@Autowired
    private  UserRepository userRepository;
	
	@Autowired
	private  ProgramRepository programRepository;

	public ActivityLog saveActivity(ActivityLogRequest request) {
		
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        //validation logic to check pass id is present on repective db or not for both user and program
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found"));
        
        //creating object of activity 
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUser(user);
        activityLog.setProgram(program);
        activityLog.setActivityDate(request.getDate());
        activityLog.setActivityType(
                ActivityType.valueOf(request.getActivityTypeId())
        );
        activityLog.setWorkContext(
                WorkContext.valueOf(request.getWorkContextId())
        );
        activityLog.setOutputCount(request.getOutputCount());
        activityLog.setNotes(request.getDescription());
        return activityLogRepository.save(activityLog);
	}

	 public List<ActivityLogResponse> getAllActivityLogs() {
	        List<ActivityLog> logs = activityLogRepository.findAll();

	        // Convert to response DTO
	        return logs.stream().map(log -> new ActivityLogResponse(
	                log.getId(),
	                log.getUser().getId(),
	                log.getUser().getName(),
	                log.getProgram().getId(),
	                log.getProgram().getName(),
	                log.getActivityDate(),
	                log.getActivityType().name(),
	                log.getWorkContext().name(),
	                log.getOutputCount(),
	                log.getNotes()
	        )).toList();
	    }

	  // Fetch user-specific activities
	    public List<ActivityLog> getUserActivities(Long userId) {
	        return activityLogRepository.findByUserId(userId);
	    }
}
