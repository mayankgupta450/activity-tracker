package com.activitytracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.activitytracker.entity.Role;
import com.activitytracker.entity.User;
import com.activitytracker.repo.RoleRepository;
import com.activitytracker.repo.UserRepository;

@SpringBootApplication
public class ActivityTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivityTrackerApplication.class, args);
		System.out.println("Application Starts.......");

	}

	//using this to create intial roles and user for login
	@Bean
	CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo) {
		return args -> {
			// Create default roles
			if (roleRepo.count() == 0) {
				roleRepo.save(new Role("ADMIN"));
				roleRepo.save(new Role("USER"));
			}

			// Create default admin
			if (userRepo.count() == 0) {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				String hashedPassword = encoder.encode("password123");

				User admin = new User();
				admin.setName("Test Admin");
				admin.setEmail("admin@khushibaby.org");
				admin.setPassword(hashedPassword);
				admin.setRole(roleRepo.findByRoleName("ADMIN").orElseThrow(() ->
			    new RuntimeException("ADMIN role not found")
			));
				userRepo.save(admin);
			}
		};
	}

}
