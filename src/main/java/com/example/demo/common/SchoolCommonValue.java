package com.example.demo.common;

import java.time.LocalDate;

public final class SchoolCommonValue {

	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String GRADE_CLASS = "grade_class";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String GRADE = "grade";
	public static final String CLASS_NAME = "class_name";
	public static final String POINT = "point";
	public static final String YEAR = "year";
	public static final String SEASON_NAME = "season_name";
	public static final String SUBJECT_NAME = "subject_name";

	public static final String ROLE = "role";
	public static final String ROLENAME = "roleName";
	public static final String USERTESTID = "userTestId";
	public static final String FIRSTNAME = "firstName";
	public static final String LASTNAME = "lastName";
	public static final String USERID = "userId";
	public static final String APPUSER = "appUser";
	public static final String USERNAME = "userName";
	public static final String GRADECLASS = "gradeClass";
	public static final String TEST = "test";
//	public static final String STUDENT = "student";
//	public static final String TEACHER = "teacher";
//	public static final String TEACHERID = "teacherId";
	public static final String CLASSNAME = "className";
	public static final String SUBJECTNAME = "subjectName";
	public static final String SEASONNAME = "seasonName";
	public static final String PASSWORD = "password";

	public static final String ROLE_STUDENT = "ROLE_STUDENT";
	public static final String ROLE_TEACHER = "ROLE_TEACHER";

	public static final int FORM_MAX_VALUE = LocalDate.now().getYear() + 2;
	public static final int FORM_MIN_VALUE = LocalDate.now().getYear() - 2;

	public static final String AUTHORITY_TEACHER = "hasAuthority('ROLE_TEACHER')";
	public static final String AUTHORITY_STUDENT = "hasAuthority('ROLE_STUDENT')";
	public static final String AUTHORITY_STUDENT_AND_TEACHER = "hasAuthority('ROLE_STUDENT') OR hasAuthority('ROLE_TEACHER')";

	public static final String BR = System.lineSeparator();
}
