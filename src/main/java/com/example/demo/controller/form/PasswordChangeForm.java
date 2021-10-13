package com.example.demo.controller.form;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PasswordChangeForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Size(min = 8, message = "パスワードは8文字以上で入力してください")
	private String password;

	@Size(min = 6, message = "ユーザー名は6文字以上で入力してください")
	@Pattern(regexp = "[\\w@.-]+", message = "使えない文字列が含まれています。使える文字はa~z,A~Z,@,_,-記号のみです")
	private String userName;

}
