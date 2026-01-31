package com.activitytracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.activitytracker.entity.Program;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

}
