package com.example.demo.domain.grade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.grade.model.UserGradeClass;
import com.example.demo.domain.grade.model.propagation.UserGradeClassView;

public interface UserGradeClassRepository
		extends JpaRepository<UserGradeClass, Long>, JpaSpecificationExecutor<UserGradeClass> {

	/**
	 * ユーザー名を元に在籍した学年・クラス情報の最新の年度・クラス名・学年情報を抽出する
	 * 
	 * @param userName ユーザー名
	 * @return
	 */
	@Query("SELECT MAX(gc.year) AS year, gc.className AS clazzName, gc.grade AS grade  FROM UserGradeClass ugc "
			+ "INNER JOIN GradeClass gc ON ugc.gradeClass = gc.gradeId "
			+ "INNER JOIN AppUser au ON ugc.appUser = au.userId WHERE au.userName =:userName")
	UserGradeClassView findUserGradeGlassView(@Param("userName") String userName);

	/**
	 * ユーザーIDと学年情報を元に、年度・クラス名・学年情報を返却する
	 * 
	 * @param userId ユーザーID
	 * @param grade  学年
	 * @return
	 */
	@Query("SELECT gc.year AS year, gc.className AS clazzName, gc.grade AS grade FROM UserGradeClass ugc "
			+ "INNER JOIN GradeClass gc ON ugc.gradeClass = gc.gradeId "
			+ "INNER JOIN AppUser au ON ugc.appUser = au.userId " + "WHERE au.userId =:userId AND gc.grade =:grade")
	UserGradeClassView findUserGradeClassView(@Param("userId") Long userId, @Param("grade") Byte grade);
}
