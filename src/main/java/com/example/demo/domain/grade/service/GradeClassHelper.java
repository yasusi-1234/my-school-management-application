package com.example.demo.domain.grade.service;

import static com.example.demo.common.SchoolCommonValue.CLASSNAME;
import static com.example.demo.common.SchoolCommonValue.GRADE;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.domain.grade.model.GradeClass;

public final class GradeClassHelper {

	/**
	 * 学年の情報が一致するデータ抽出用
	 * 
	 * @param grade
	 * @return
	 */
	public static Specification<GradeClass> equalGrade(Integer grade) {
		return grade == null ? null : (root, query, cb) -> cb.equal(root.get(GRADE), grade);
	}

	/**
	 * クラス名が一致するデータ抽出用
	 * 
	 * @param className
	 * @return
	 */
	public static Specification<GradeClass> equalClassName(String className) {
		return !StringUtils.hasText(className) ? null : (root, query, cb) -> cb.equal(root.get(CLASSNAME), className);
	}

	/**
	 * 年度が一致するデータ抽出用
	 * 
	 * @param year
	 * @return
	 */
	public static Specification<GradeClass> equalYear(int year) {
		return (root, query, cb) -> cb.equal(root.get(YEAR), year);
	}

	/**
	 * クラス名の配列の中身と一致する要素の抽出
	 * 
	 * @param clazzList
	 * @return
	 */
	public static Specification<GradeClass> equalClassNames(List<Clazz> clazzList) {
		return (root, query, cb) -> {
			List<String> classNames = clazzList.stream().map(c -> c.name()).collect(Collectors.toList());
			return root.get(CLASSNAME).in(classNames);
		};
	}

	/**
	 * クラス名の配列の中身と一致する要素の抽出
	 *
	 * @param clazzList
	 * @return
	 */
	public static Specification<GradeClass> equalStringClassNames(List<String> clazzList) {
		return (root, query, cb) -> root.get(CLASSNAME).in(clazzList);
	}

}
