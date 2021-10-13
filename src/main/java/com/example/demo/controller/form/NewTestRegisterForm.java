package com.example.demo.controller.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class NewTestRegisterForm {

	@Min(value = 1900, message = "値が選ばれていません。選択してください")
	@Max(value = 2500, message = "値が選ばれていません。選択してください")
	private int year;
}
