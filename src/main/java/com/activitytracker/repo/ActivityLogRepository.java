package com.activitytracker.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.activitytracker.entity.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	List<ActivityLog> findByUserId(Long userId);

	@Query(value = """
			    SELECT
			        p.name AS programName,
			        SUM(a.output_count) AS totalOutput
			    FROM activity_logs a
			    JOIN programs p ON a.program_id = p.id
			    GROUP BY p.name
			""", nativeQuery = true)
	List<Map<String, Object>> getProgramWiseOutput();

	@Query(value = """
			SELECT
			    p.name AS programName,
			    COUNT(a.id) AS activityCount
			FROM activity_logs a
			JOIN programs p ON a.program_id = p.id
			GROUP BY p.name
			""", nativeQuery = true)
	List<Map<String, Object>> getProgramActivityCount();

}
