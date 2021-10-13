package com.example.demo.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class StandardDeviation {
	@JsonProperty("y")
	private Integer numberOfPeaple;
	@JsonProperty("x")
	private Integer point;
}
