package com.example.demo.controller.form;

import java.io.Serializable;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;

import lombok.Data;

@Data
public class StudentSearchForm implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private Integer year;

	private Grade grade;

	private Clazz clazz;

	private String lastName;

	private String firstName;

}
