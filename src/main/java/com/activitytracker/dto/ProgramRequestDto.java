package com.activitytracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProgramRequestDto {

	@NotBlank(message = "Program name is required")
	private String name;

	@NotBlank(message = "Description is required")
	private String description;

}
