package com.example.demo.domain.excel.exception;

import java.util.List;

public class RequiredHeaderValueNotExistException extends RuntimeException {

	private List<String> requiredHeaderList;

	private static final long serialVersionUID = 1L;

	public RequiredHeaderValueNotExistException(String message, List<String> requiredHeaderList, Throwable throwable) {
		// TODO 自動生成されたコンストラクター・スタブ
		super(message, throwable);
		this.requiredHeaderList = requiredHeaderList;
	}

	public RequiredHeaderValueNotExistException(String message, List<String> requiredHeaderList) {
		super(message);
		this.requiredHeaderList = requiredHeaderList;
	}

	public List<String> getRequiredHeaderList() {
		return this.requiredHeaderList;
	}

}
