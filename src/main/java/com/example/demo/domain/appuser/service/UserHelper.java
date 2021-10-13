package com.example.demo.domain.appuser.service;

import static com.example.demo.common.SchoolCommonValue.USERID;
import static com.example.demo.common.SchoolCommonValue.USERNAME;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.demo.domain.appuser.model.AppUser;

public final class UserHelper {

	/**
	 * 指定されたUserIdのリストから抽出する
	 * 
	 * @param userIds
	 * @return
	 */
	public static Specification<AppUser> inUserId(List<Long> userIds) {
		return CollectionUtils.isEmpty(userIds) ? null : (root, query, cb) -> root.get(USERID).in(userIds);
	}

	/**
	 * 指定されたUserNameと一致する要素を抽出する
	 * 
	 * @param userName
	 * @return
	 */
	public static Specification<AppUser> equalUserName(String userName) {
		return StringUtils.hasLength(userName) ? (root, query, cb) -> cb.equal(root.get(USERNAME), userName) : null;
	}
}
