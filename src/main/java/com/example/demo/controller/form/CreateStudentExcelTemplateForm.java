package com.example.demo.controller.form;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.validator.NoNull;

import lombok.Data;

@Data
public class CreateStudentExcelTemplateForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Min(value = 2000, message = "値が選択されていません")
	@Max(value = 2050, message = "値が選択されていません")
	private int year;
	@NoNull
	private Grade grade;
}