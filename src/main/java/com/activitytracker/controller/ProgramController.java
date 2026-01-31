package com.activitytracker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<ProgramResponseDto>> getAllPrograms() {
        List<ProgramResponseDto> programs = programService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }
	

}
