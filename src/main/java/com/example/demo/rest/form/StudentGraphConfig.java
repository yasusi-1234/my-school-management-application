package com.example.demo.rest.form;

import java.io.Serializable;
import java.util.List;

import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudentGraphConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Grade gradeOption;

	private List<Season> seasonOption;

	private Subject subjectOption;

	public void addSeasonOption(Season season) {
		this.seasonOption.add(season);
	}

}
