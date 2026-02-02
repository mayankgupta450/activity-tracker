package com.activitytracker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.activitytracker.dto.RegisterRequestDto;
import com.activitytracker.dto.UserResponseDto;
import com.activitytracker.entity.Program;
import com.activitytracker.entity.Role;
import com.activitytracker.entity.User;
import com.activitytracker.entity.UserProgram;
import com.activitytracker.repo.ProgramRepository;
import com.activitytracker.repo.RoleRepository;
import com.activitytracker.repo.UserProgramRepository;
import com.activitytracker.repo.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	private UserProgramRepository userProgramRepository;

	public Map<String, Object> registerUser(RegisterRequestDto request) {

		// Email duplicate check
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		Long roleId;
		if ("1".equals(request.getRole())) {
			roleId = 1L;
		} else if ("2".equals(request.getRole())) {
			roleId = 2L;
		} else {
			throw new RuntimeException("Invalid role");
		}

		// getting role object from db
		Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

		// Create user object
		User user = new User();
		user.setName(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword())); // encoding password not tostore plin text
																			// password
		user.setRole(role);


		List<UserProgram> userPrograms = new ArrayList<>();
		boolean isAdmin = "ADMIN".equalsIgnoreCase(role.getRoleName());
		if (!isAdmin) {
		        //  non admin have must to program id
		        if (request.getProgramIds() == null || request.getProgramIds().isEmpty()) {
		            throw new RuntimeException("Program is mandatory for non-admin users");
		        }
		    }

		User savedUser = userRepository.save(user);  //saving user inton table db
		//getting program id multiple from ui thats why looping done
		if (request.getProgramIds() != null && !request.getProgramIds().isEmpty()) {
		for (Long programId : request.getProgramIds()) {
			Program program = programRepository.findById(programId)
					.orElseThrow(() -> new RuntimeException("Program not found"));

			UserProgram up = new UserProgram();
			up.setUser(savedUser);
			up.setProgram(program);
			userPrograms.add(up);
		}

		//now saving data in program table in db
		userProgramRepository.saveAll(userPrograms);
		}
		
		List<String> programNames = userPrograms.stream().map(up -> up.getProgram().getName()).toList();

		List<Long> programIds = userPrograms.stream().map(up -> up.getProgram().getId()).toList();
		// response creation for ui send
		Map<String, Object> response = new HashMap<>();
		response.put("id", savedUser.getId());
		response.put("username", savedUser.getName());
		response.put("email", savedUser.getEmail());
		response.put("role", savedUser.getRole().getRoleName());
		response.put("programIds", programIds); 
		response.put("programNames", programNames); 
		return response;
	}

	public List<UserResponseDto> getAllUsers() {

		return userRepository.findAll().stream().map(user -> {
			List<UserProgram> userPrograms = userProgramRepository.findProgramsByUserId(user.getId());
	        List<Long> programIds = userPrograms.stream()
	                .map(up -> up.getProgram().getId())
	                .toList();

	        List<String> programNames = userPrograms.stream()
	                .map(up -> up.getProgram().getName())
	                .toList();
	        return new UserResponseDto(
	                user.getId(),
	                user.getName(),
	                user.getEmail(),
	                user.getRole().getRoleName(),
	                programIds,
	                programNames
	        );
		}).toList();
	}

}
