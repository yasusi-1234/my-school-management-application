package com.example.demo.domain.appuser.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class AppUser implements Serializable {

	private static final long serialVersionUID = -8451913692451614219L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	@Column(nullable = false, length = 50, unique = true)
	private String userName;
	@Column(nullable = false, length = 255)
	private String password;
	@Column(nullable = false, length = 20)
	private String firstName;
	@Column(nullable = false, length = 20)
	private String lastName;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "roleId")
	private Role role;

	/**
	 * 
	 * @param userName
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param role
	 * @return
	 */
	public static AppUser of(String userName, String password, String firstName, String lastName, Role role) {
		AppUser appUser = new AppUser();
		appUser.setUserName(userName);
		appUser.setPassword(password);
		appUser.setFirstName(firstName);
		appUser.setLastName(lastName);
		appUser.setRole(role);
		return appUser;
	}

	/**
	 * 
	 * @param userId
	 * @param userName
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param role
	 * @return
	 */
	public static AppUser of(Long userId, String userName, String password, String firstName, String lastName,
			Role role) {
		AppUser appUser = new AppUser();
		appUser.setUserId(userId);
		appUser.setUserName(userName);
		appUser.setPassword(password);
		appUser.setFirstName(firstName);
		appUser.setLastName(lastName);
		appUser.setRole(role);
		return appUser;
	}

	/**
	 * 
	 * @param userId
	 * @param role
	 * @return
	 */
	public static AppUser of(Long userId, Role role) {
		AppUser appUser = new AppUser();
		appUser.setUserId(userId);
		appUser.setRole(role);
		return appUser;
	}
}
