package com.example.demo.domain.excel.exception;

import java.util.List;
import java.util.Map;

public class RequiredBodyValueNotExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Integer, List<String>> notExistMap;

	public RequiredBodyValueNotExistException(String message, Map<Integer, List<String>> notExistMap) {
		super(message);
		this.notExistMap = notExistMap;
	}

	public Map<Integer, List<String>> getNotExistMap() {
		return this.notExistMap;
	}

}
