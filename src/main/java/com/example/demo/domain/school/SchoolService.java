package com.example.demo.domain.school;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;
import com.example.demo.controller.form.GradeClassForm;
import com.example.demo.controller.form.SchoolDataForm;
import com.example.demo.controller.form.SurveyRecordForm;
import com.example.demo.domain.appuser.model.AppUser;
import com.example.demo.domain.grade.model.GradeClass;
import com.example.demo.domain.grade.model.UserGradeClass;
import com.example.demo.domain.grade.model.propagation.UserGradeClassView;
import com.example.demo.domain.test.model.Test;
import com.example.demo.domain.test.model.UserTest;
import com.example.demo.domain.test.model.propagation.UserTestFunctionView;
import com.example.demo.domain.test.model.propagation.UserTestView;
import org.springframework.transaction.annotation.Transactional;

public interface SchoolService {

	List<UserGradeClass> insertAllGradeClass(SchoolDataForm form);

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    List<UserGradeClass> updateAllGradeClasses(SchoolDataForm form);

    List<UserTest> insertAllStudentTest(SchoolDataForm form);

	List<UserTest> updateAllStudentTest(SchoolDataForm form);

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    List<GradeClass> createGradeClassInformation(GradeClassForm form);

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    List<GradeClass> createGradeClassInformation(int year, int grade, List<String> classNames);

    List<Test> createTestInformationOfYear(int year);

	UserTest findByUserTestId(Long studentId);

    UserTest singleUpdateUserTest(Long userTestId, int newPoint);

	Page<UserTest> findAllRequestTestInformation(SurveyRecordForm form, Pageable pageable);

	List<UserTest> findAllSingleStudentTests(Long studentId, Grade grade, Subject subject, List<Season> seasons);

	Page<UserGradeClass> findSearchGradeClass(int year, Grade grade, Clazz clazz, String lastName, String firstName,
			Pageable pageable);

	List<UserGradeClass> findSearchGradeClass(int year, Grade grade, Clazz clazz);

	Page<UserTestView> findAllTestUserView(int year, int grade, String season, Pageable pageable);

	UserTestFunctionView findAllTestUserFunctionView(int year, int grade, String season);

	Page<UserTestView> findAllTestUserView(int year, int grade, String className, String season, Pageable pageable);

	UserTestFunctionView findAllTestUserFunctionView(int year, int grade, String className, String season);

	/**
	 * ユーザー名を元に在籍した学年・クラス情報の最新の年度・クラス名・学年情報を抽出する
	 * 
	 * @param userName
	 * @return
	 */
	UserGradeClassView findUserGradeClassView(String userName);

	/**
	 * 年度情報を元にその年度の登録されているテスト結果の時期情報を取得し返却する
	 * 
	 * @param year 年度
	 * @return
	 */
	List<String> findAllSeasonByYear(int year);

	/**
	 * 年度情報を元にその年度の登録されているテスト結果の時期情報を取得し返却する
	 * 
	 * @param year      年度
	 * @param grade     学年
	 * @param className クラス名
	 * @return
	 */
	List<String> findAllSeasonByYear(int year, int grade, String className);

	long finduserNameCount(String userName);

	AppUser saveAppUser(AppUser appUser, String newPassword, String newUserName);

}
