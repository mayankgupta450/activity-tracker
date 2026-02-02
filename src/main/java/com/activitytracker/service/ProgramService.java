package com.activitytracker.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.activitytracker.dto.ProgramRequestDto;
import com.activitytracker.dto.ProgramResponseDto;
import com.activitytracker.entity.Program;
import com.activitytracker.entity.User;
import com.activitytracker.entity.UserProgram;
import com.activitytracker.repo.ProgramRepository;
import com.activitytracker.repo.UserProgramRepository;
import com.activitytracker.repo.UserRepository;

@Service
public class ProgramService {

	@Autowired
	ProgramRepository programRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserProgramRepository userProgramRepository;
	
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

    @Transactional
	public void updateUserPrograms(Long userId, List<Long> programIds) {
		 User user = userRepository.findById(userId)
		            .orElseThrow(() -> new RuntimeException("User not found"));

		    // remove existing entry from  db
		 userProgramRepository.deleteByUser_Id(userId);
		 Set<Long> uniqueProgramIds = new HashSet<>(programIds);

		    // add new mappings
		    for (Long programId : uniqueProgramIds) {
		        Program program = programRepository.findById(programId)
		                .orElseThrow(() -> new RuntimeException("Program not found"));

		        UserProgram up = new UserProgram();
		        up.setUser(user);
		        up.setProgram(program);
		        userProgramRepository.save(up);
		    }
		
	}
}
