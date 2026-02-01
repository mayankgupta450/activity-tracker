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
public class ActivityLogRequest {
	private Long userId;
	private Long programId;
	private String activityTypeId;
	private String workContextId;
	private String description;
	private LocalDate date;
	private Integer outputCount;

}
