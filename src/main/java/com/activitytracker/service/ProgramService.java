package com.activitytracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activitytracker.dto.ProgramRequestDto;
import com.activitytracker.dto.ProgramResponseDto;
import com.activitytracker.entity.Program;
import com.activitytracker.repo.ProgramRepository;

@Service
public class ProgramService {

	@Autowired
	ProgramRepository programRepository;

	// Create program
	public ProgramResponseDto createProgram(ProgramRequestDto request) {
		Program program = new Program();
		program.setName(request.getName());
		program.setDescription(request.getDescription());

		Program saved = programRepository.save(program);

		return new ProgramResponseDto(saved.getId(), saved.getName(), saved.getDescription());
	}

    public List<ProgramResponseDto> getAllPrograms() {
        return programRepository.findAll()
                .stream()
                .map(p -> new ProgramResponseDto(p.getId(), p.getName(), p.getDescription()))
                .collect(Collectors.toList());
    }
}
