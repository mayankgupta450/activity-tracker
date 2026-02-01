package com.activitytracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.activitytracker.entity.ActivityLog;

public interface ActivityLogRepository  extends JpaRepository<ActivityLog, Long> {

}
