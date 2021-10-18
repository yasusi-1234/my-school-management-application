package com.example.demo.domain.test.service.helper;

import static com.example.demo.common.SchoolCommonValue.SEASONNAME;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.demo.domain.test.model.Test;

public final class TestHelper {

	/**
	 * 年度情報が一致するものを抽出
	 * 
	 * @param year
	 * @return
	 */
	public static Specification<Test> equalYear(int year) {
		return (root, query, cb) -> cb.equal(root.get(YEAR), year);
	}

	/**
	 * テスト時期情報と一致するものを抽出
	 * 
	 * @param season
	 * @return
	 */
	public static Specification<Test> equalSeason(String season) {
		return StringUtils.hasText(season) ? (root, query, cb) -> cb.equal(root.get(SEASONNAME), season) : null;
	}

}
