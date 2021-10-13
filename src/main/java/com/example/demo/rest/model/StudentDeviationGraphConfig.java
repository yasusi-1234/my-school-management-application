package com.example.demo.rest.model;

import java.io.Serializable;
import java.util.Objects;

import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;

import lombok.Data;

@Data
public class StudentDeviationGraphConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private Grade gradeOption;

	private Season seasonOption;

	private Subject subjectOption;

	public boolean allFieldNotNull() {
		return Objects.nonNull(this.gradeOption) && Objects.nonNull(this.seasonOption)
				&& Objects.nonNull(this.subjectOption);
	}
}
