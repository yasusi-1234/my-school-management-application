package com.example.demo.domain.school.exception;

/**
 * 学年クラスの情報がDBにまだ登録されていない場合に呼び出される例外
 * 
 */
public class GradeClassNotRegisterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7415950011354491926L;

	public GradeClassNotRegisterException(String message) {
		super(message);
	}

}
