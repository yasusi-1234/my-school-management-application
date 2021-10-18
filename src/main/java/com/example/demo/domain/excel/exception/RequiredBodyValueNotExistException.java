package com.example.demo.domain.excel.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public Map<Integer, String> getConvertNotExistMap(){
		return this.notExistMap.entrySet()
				.stream().collect(Collectors.toMap(
						Map.Entry::getKey,
						item -> String.join(",", item.getValue())));
	}

}
