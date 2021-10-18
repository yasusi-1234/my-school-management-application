package com.example.demo.domain.grade.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class GradeClass implements Serializable {

	private static final long serialVersionUID = 5316959841092715600L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gradeId;
	@Column(nullable = false)
	private int grade;
	@Column(nullable = false, length = 1)
	private String className;
	@Column(nullable = false)
	private int year;

	/**
	 * gradeId無しの学年・クラス名・年度を持ったGradeオブジェクトを生成するメソッド
	 * @param grade 学年
	 * @param className クラス名
	 * @param year 年度
	 * @return gradeId無しのGradeClassオブジェクト
	 */
	public static GradeClass of(int grade, String className, int year){
		GradeClass gradeClass = new GradeClass();
		gradeClass.setGrade(grade);
		gradeClass.setClassName(className);
		gradeClass.setYear(year);
		return gradeClass;
	}
}
