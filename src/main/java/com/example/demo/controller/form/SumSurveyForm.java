package com.example.demo.controller.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.NumDisplay;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.SortSumPoint;
import com.example.demo.controller.form.validator.NoNull;

import lombok.Data;

@Data
public class SumSurveyForm {

	@Min(value = 2000, message = "値が選択されていません")
	@Max(value = 2100, message = "値が選択されていません")
	private Integer year;
	@NoNull(message = "値が選択されていません")
	private Grade grade;

	@NoNull(message = "値が選択されていません")
	private Clazz clazz;

	@NoNull(message = "値が選択されていません")
	private Season season;

	@NoNull(message = "値が選択されていません")
	private NumDisplay display = NumDisplay.MIDDLE;

	@NoNull(message = "値が選択されていません")
	private SortSumPoint sort = SortSumPoint.DESC;

	public boolean allNonNull() {
		return this.year != null && this.grade != null && this.clazz != null && this.season != null;
	}

	public boolean yearGradeClazzIsNull() {
		return this.year == null && this.grade == null && this.clazz == null;
	}

}
