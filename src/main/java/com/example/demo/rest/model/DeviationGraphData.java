package com.example.demo.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DeviationGraphData {

	@JsonProperty("label")
	private String label;

	@JsonProperty("data")
	private List<StandardDeviation> standardDeviationList = new ArrayList<>();

	public void addStandardDeviation(StandardDeviation standardDeviation) {
		this.standardDeviationList.add(standardDeviation);
	}
}
