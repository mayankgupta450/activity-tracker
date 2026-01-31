package com.activitytracker.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.activitytracker.dto.RegisterRequestDto;
import com.activitytracker.entity.Role;
import com.activitytracker.entity.User;
import com.activitytracker.repo.RoleRepository;
import com.activitytracker.repo.UserRepository;

@Service
public class UserService {
	
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> registerUser(RegisterRequestDto request) {

        //  Email duplicate check
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

        //getting role object from db
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Create user object
        User user = new User();
        user.setName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); //encoding password not tostore plin text password
        user.setRole(role);

        User savedUser = userRepository.save(user);

        //response creation for ui send
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("username", savedUser.getName());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().getRoleName());

        return response;
    }

}
