package com.activitytracker.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
	private Long id;
	private Long userId;
	private String userName;
	private Long programId;
	private String programName;
	private LocalDate activityDate;
	private String activityType;
	private String workContext;
	private Integer outputCount;
	private String notes;

}
