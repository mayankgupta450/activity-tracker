package com.activitytracker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.activitytracker.dto.ProgramRequestDto;
import com.activitytracker.dto.ProgramResponseDto;
import com.activitytracker.service.ProgramService;

import jakarta.validation.Valid;

@RestController
public class ProgramController {
	
	@Autowired
	ProgramService programService;
	
	
	  // Create program
    @PostMapping("/api/admin/programs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProgram(
            @Valid @RequestBody ProgramRequestDto request,
            BindingResult bindingResult) {

        // Validation checking
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors()
                    .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        ProgramResponseDto response = programService.createProgram(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all programs
    @GetMapping("/api/admin/programs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProgramResponseDto>> getAllPrograms() {
        List<ProgramResponseDto> programs = programService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }
	
    @PutMapping("/api/admin/{userId}/programs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserPrograms(
            @PathVariable Long userId,
            @RequestBody List<Long> programIds) {
    	programService.updateUserPrograms(userId, programIds);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Programs updated successfully");
        return ResponseEntity.ok(response);
    }


}
