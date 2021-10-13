package com.example.demo.controller.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;
import com.example.demo.controller.form.validator.NoNull;

import lombok.Data;

@Data
public class CreateExcelTemplateForm {

	@Min(value = 2000, message = "値が選択されていません")
	@Max(value = 2050, message = "値が選択されていません")
	private int year;
	@NoNull
	private Grade grade;
	@NoNull
	private Clazz clazz;
	@NoNull
	private Season season;
	@NoNull
	private Subject subject;
}
