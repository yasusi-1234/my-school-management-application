package com.example.demo.controller.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.validator.ListNoNull;
import com.example.demo.controller.form.validator.NoNull;

import lombok.Data;

@Data
public class GradeClassForm {

	@Min(value = 1950, message = "値を選択してください")
	@Max(value = 2500, message = "値を選択してください")
	private int year;
	@NoNull(message = "値を選択してください")
	private Grade grade;
	@ListNoNull(message = "1つ以上選択してください")
	private List<Clazz> classList = new ArrayList<>();

	public void addClassList(Clazz clazz) {
		classList.add(clazz);
	}
}
