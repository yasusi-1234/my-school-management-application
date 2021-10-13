package com.example.demo.domain.test.service.helper;

import static com.example.demo.common.SchoolCommonValue.APPUSER;
import static com.example.demo.common.SchoolCommonValue.CLASSNAME;
import static com.example.demo.common.SchoolCommonValue.GRADE;
import static com.example.demo.common.SchoolCommonValue.SEASONNAME;
import static com.example.demo.common.SchoolCommonValue.SUBJECTNAME;
import static com.example.demo.common.SchoolCommonValue.TEST;
import static com.example.demo.common.SchoolCommonValue.USERID;
import static com.example.demo.common.SchoolCommonValue.USERTESTID;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;
import com.example.demo.domain.test.model.UserTest;

public final class UserTestHelper {

	/**
	 * ユーザーとテスト情報をFetch取得する
	 * 
	 * @return
	 */
	public static Specification<UserTest> fetchUserAndTest() {
		return (root, query, cb) -> {
			if (query.getResultType() == Long.class) {
				root.join(APPUSER);
				root.join(TEST);
			} else {
				root.fetch(APPUSER);
				root.fetch(TEST);
			}
			return query.getRestriction();
		};
	}

	/**
	 * userTestId情報と一致するものを抽出
	 * 
	 * @param userTestId
	 * @return
	 */
	public static Specification<UserTest> equalUserTestId(Long userTestId) {
		return userTestId == null ? null : (root, query, cb) -> cb.equal(root.get(USERTESTID), userTestId);
	}

	/**
	 * userId情報と一致するものを抽出
	 * 
	 * @param appUserId
	 * @return
	 */
	public static Specification<UserTest> equalAppUserId(Long appUserId) {
		return appUserId == null ? null : (root, query, cb) -> cb.equal(root.get(APPUSER).get(USERID), appUserId);
	}

	/**
	 * 指定された時期名と一致する情報を抽出
	 * 
	 * @param seasonName
	 * @return
	 */
	public static Specification<UserTest> equalSeasonName(String seasonName) {
		return StringUtils.hasText(seasonName)
				? (root, query, cb) -> cb.equal(root.get(TEST).get(SEASONNAME), seasonName)
				: null;
	}

	/**
	 * 教科名と一致する情報を抽出
	 * 
	 * @param subjectName
	 * @return
	 */
	public static Specification<UserTest> equalSubjectName(String subjectName) {
		return StringUtils.hasText(subjectName)
				? (root, query, cb) -> cb.equal(root.get(TEST).get(SUBJECTNAME), subjectName)
				: null;
	}

	/**
	 * 年度が一致する情報を抽出
	 * 
	 * @param year
	 * @return
	 */
	public static Specification<UserTest> equalYear(int year) {
		return (root, query, cb) -> cb.equal(root.get(TEST).get(YEAR), year);
	}

	/**
	 * 学年が一致する情報を抽出(複数In句)
	 * 
	 * @param grades
	 * @return
	 */
	public static Specification<UserTest> inGrade(List<Byte> grades) {
		return CollectionUtils.isEmpty(grades) ? null : (root, query, cb) -> root.get(TEST).get(GRADE).in(grades);
	}

	/**
	 * 学年が一致する情報を抽出
	 * 
	 * @param grade
	 * @return
	 */
	public static Specification<UserTest> equalGrade(byte grade) {
		return (root, query, cb) -> root.get(TEST).get(GRADE).in(grade);
	}

	/**
	 * 学年が一致する情報を抽出
	 * 
	 * @param grade
	 * @return
	 */
	public static Specification<UserTest> equalGrade(Grade grade) {
		return grade == null ? null : equalGrade((byte) grade.getGrade());
	}

	/**
	 * クラスが一致する情報を抽出
	 * 
	 * @param clazz
	 * @return
	 */
	public static Specification<UserTest> equalClass(Clazz clazz) {
		return clazz == null ? null : (root, query, cb) -> {
			if (clazz == Clazz.ALL) {
				// 全てのクラスが選択されている場合
				List<String> classes = clazz.valuesExcludeAll().stream().map(c -> c.name())
						.collect(Collectors.toList());
				return root.get(CLASSNAME).in(classes);
			}
			// 単項目のクラスの場合
			return cb.equal(root.get(CLASSNAME), clazz.name());
		};
	}

	/**
	 * クラスが一致する情報を抽出
	 * 
	 * @param className
	 * @return
	 */
	public static Specification<UserTest> equalClass(String className) {
		return StringUtils.hasText(className) ? (root, query, cb) -> cb.equal(root.get(CLASSNAME), className) : null;
	}

	/**
	 * 時期情報が一致する情報を抽出
	 * 
	 * @param seasons
	 * @return
	 */
	public static Specification<UserTest> equalSeasons(List<Season> seasons) {
		return CollectionUtils.isEmpty(seasons) ? null : (root, query, cb) -> {
			List<String> seasonList = seasons.stream().map(s -> s.getSeasonName()).collect(Collectors.toList());
			return root.get(TEST).get(SEASONNAME).in(seasonList);
		};
	}

	/**
	 * 教科情報が一致する情報を抽出
	 * 
	 * @param subject
	 * @return
	 */
	public static Specification<UserTest> equalSubject(Subject subject) {
		return subject == null ? null : (root, query, cb) -> {
			if (subject == Subject.ALL) {
				// 全ての教科が選択されている場合
				List<String> subjects = subject.valuesExcludeAll().stream().map(sub -> sub.getSubjectName())
						.collect(Collectors.toList());
				return root.get(TEST).get(SUBJECTNAME).in(subjects);
			}
			return cb.equal(root.get(TEST).get(SUBJECTNAME), subject.getSubjectName());
		};
	}
}
