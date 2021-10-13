package com.example.demo.domain.excel.exception;

import java.util.List;

import lombok.Getter;

public class PointAbnormalException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	List<Integer> abnormalPointIndex;

	public PointAbnormalException(String message, List<Integer> abnormalPointIndex) {
		super(message);
		this.abnormalPointIndex = abnormalPointIndex;
	}

}
