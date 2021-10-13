package com.example.demo.domain.grade.service;

import static com.example.demo.common.SchoolCommonValue.APPUSER;
import static com.example.demo.common.SchoolCommonValue.CLASSNAME;
import static com.example.demo.common.SchoolCommonValue.FIRSTNAME;
import static com.example.demo.common.SchoolCommonValue.GRADE;
import static com.example.demo.common.SchoolCommonValue.GRADECLASS;
import static com.example.demo.common.SchoolCommonValue.LASTNAME;
import static com.example.demo.common.SchoolCommonValue.ROLE;
import static com.example.demo.common.SchoolCommonValue.ROLENAME;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.domain.grade.model.UserGradeClass;

public final class UserGradeClassHelper {

	/**
	 * ユーザーをジョインフェッチ
	 * 
	 * @return
	 */
	public static Specification<UserGradeClass> fetchUser() {
		return (root, query, cb) -> {
			if (query.getResultType() == Long.class) {
				root.join(APPUSER);
			} else {
				root.fetch(APPUSER);
			}
			return query.getRestriction();
		};
	}

	/**
	 * グレードクラスをジョインフェッチ
	 * 
	 * @return
	 */
	public static Specification<UserGradeClass> fetchGradeClass() {
		return (root, query, cb) -> {
			if (query.getResultType() == Long.class) {
				root.join(GRADECLASS);
			} else {
				root.fetch(GRADECLASS);
			}
			return query.getRestriction();
		};
	}

	/**
	 * グレードクラスとユーザーをジョインフェッチ
	 * 
	 * @return
	 */
	public static Specification<UserGradeClass> fetchGradeClassAndUser() {
		return (root, query, cb) -> {
			if (query.getResultType() == Long.class) {
				root.join(GRADECLASS);
				root.join(APPUSER);
			} else {
				root.fetch(GRADECLASS);
				root.fetch(APPUSER);
			}
			return query.getRestriction();
		};
	}

	/**
	 * グレードクラスとユーザーをジョインフェッチ
	 * 
	 * @return
	 */
	public static Specification<UserGradeClass> fetchAll() {
		return (root, query, cb) -> {
			if (query.getResultType() == Long.class) {
				root.join(GRADECLASS);
				root.join(APPUSER).join(ROLE);
			} else {
				root.fetch(GRADECLASS);
				root.fetch(APPUSER).fetch(ROLE);
			}
			return query.getRestriction();
		};
	}

	/**
	 * 年度情報の一致するものを抽出
	 */
	public static Specification<UserGradeClass> equalYear(int year) {
		return (root, query, cb) -> cb.equal(root.get(GRADECLASS).get(YEAR), year);
	}

	/**
	 * 学年情報の一致するものを抽出
	 * 
	 * @param grade
	 * @return
	 */
	public static Specification<UserGradeClass> equalGrade(Grade grade) {
		return grade == null ? null
				: (root, query, cb) -> cb.equal(root.get(GRADECLASS).get(GRADE), (byte) grade.getGrade());
	}

	/**
	 * クラス名が一致するものを抽出
	 * 
	 * @param clazz
	 * @return
	 */
	public static Specification<UserGradeClass> equalClass(Clazz clazz) {
		return clazz == null ? null : (root, query, cb) -> {
			if (clazz == Clazz.ALL) {
				List<String> classes = clazz.valuesExcludeAll().stream().map(c -> c.name())
						.collect(Collectors.toList());
				return root.get(GRADECLASS).get(CLASSNAME).in(classes);
			}
			return cb.equal(root.get(GRADECLASS).get(CLASSNAME), clazz.name());
		};
	}

	/**
	 * 指定された名前が含まれるものを抽出
	 * 
	 * @param firstName
	 * @return
	 */
	public static Specification<UserGradeClass> likeFirstName(String firstName) {
		return StringUtils.hasText(firstName)
				? (root, query, cb) -> cb.like(root.get(APPUSER).get(FIRSTNAME), "%" + firstName + "%")
				: null;
	}

	/**
	 * 指定された苗字が含まれるものを抽出
	 * 
	 * @param lastName
	 * @return
	 */
	public static Specification<UserGradeClass> likeLastName(String lastName) {
		return StringUtils.hasText(lastName)
				? (root, query, cb) -> cb.like(root.get(APPUSER).get(LASTNAME), "%" + lastName + "%")
				: null;
	}

	/**
	 * 指定されたロール名の要素を抽出
	 * 
	 * @param roleName
	 * @return
	 */
	public static Specification<UserGradeClass> equalRole(String roleName) {
		return (root, query, cb) -> cb.equal(root.get(APPUSER).get(ROLE).get(ROLENAME), roleName);
	}

}
