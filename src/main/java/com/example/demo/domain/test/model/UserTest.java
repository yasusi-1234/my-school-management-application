package com.example.demo.domain.test.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.demo.domain.appuser.model.AppUser;

import lombok.Data;

@Entity
@Data
public class UserTest implements Serializable {

	private static final long serialVersionUID = 8814390401415179872L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userTestId;
	@Column(nullable = false, length = 1)
	private String className;
	@Column(nullable = false)
	private int point;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private AppUser appUser;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "testId")
	private Test test;

	/**
	 * 
	 * @param className
	 * @param point
	 * @param user
	 * @param test
	 * @return
	 */
	public static UserTest of(String className, int point, AppUser user, Test test) {
		UserTest userTest = new UserTest();
		userTest.setClassName(className);
		userTest.setPoint(point);
		userTest.setAppUser(user);
		userTest.setTest(test);
		return userTest;
	}
}
