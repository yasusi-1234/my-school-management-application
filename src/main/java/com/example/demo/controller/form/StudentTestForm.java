package com.example.demo.controller.form;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class StudentTestForm implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private Long StudentTestId;

	@Min(value = 0, message = "値は0以上100以下で入力してください")
	@Max(value = 100, message = "値は0以上100以下で入力してください")
	private Integer point;

}
