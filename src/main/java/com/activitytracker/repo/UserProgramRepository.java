package com.activitytracker.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.activitytracker.entity.UserProgram;

@Repository
public interface UserProgramRepository extends JpaRepository<UserProgram, Long> {

	@Query(value = "SELECT * FROM user_programs WHERE user_id = :userId", nativeQuery = true)
	List<UserProgram> findProgramsByUserId(@Param("userId") Long userId);

	boolean existsByUser_IdAndProgram_Id(Long userId, Long programId);

	@Modifying
	@Transactional
	@Query("DELETE FROM UserProgram up WHERE up.user.id = :userId")
	void deleteByUser_Id(@Param("userId") Long userId);

}
