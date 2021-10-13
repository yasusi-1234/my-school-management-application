package com.example.demo.controller.form;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;

import lombok.Data;

@Data
public class SurveyStudentRecordForm {

	private Grade grade;

	private List<Season> season = new ArrayList<>();

	private Subject subject;

	public boolean allunselected() {
		return this.grade == null && season.isEmpty() && subject == null;
	};

	public void addSeason(Season season) {
		this.season.add(season);
	}
}
