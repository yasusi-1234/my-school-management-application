package com.example.demo.domain.excel.exception;

/**
 * Excelのヘッダーに対応するデータが一つも入力されていない場合に送出する例外
 * 
 * @author yasuyasu
 *
 */
public class RequestDataBodyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6852073775727026150L;

	public RequestDataBodyNotFoundException(String message) {
		super(message);
	}
}
