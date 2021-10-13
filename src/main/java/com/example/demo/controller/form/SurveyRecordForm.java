package com.example.demo.controller.form;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.SortClass;
import com.example.demo.controller.form.FormEnum.SortPoint;
import com.example.demo.controller.form.FormEnum.Subject;

import lombok.Data;

@Data
public class SurveyRecordForm {

	private int year;

	private Grade grade;

	private Clazz clazz;

	private List<Season> season = new ArrayList<>();

	private Subject subject;

	boolean firstAccess = true;

	private SortPoint sortPointOption = SortPoint.NONE;

	private SortClass sortClassOption = SortClass.NONE;

	public void addSeason(Season season) {
		this.season.add(season);
	}
}
