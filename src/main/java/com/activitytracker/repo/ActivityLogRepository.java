package com.activitytracker.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.activitytracker.entity.ActivityLog;

public interface ActivityLogRepository  extends JpaRepository<ActivityLog, Long> {

	List<ActivityLog> findByUserId(Long userId);

}
