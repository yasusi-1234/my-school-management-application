package com.example.demo.domain.test.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.test.model.UserTest;
import com.example.demo.domain.test.model.propagation.AllClassAndSubjectView;
import com.example.demo.domain.test.model.propagation.AllClassOneSubjectView;
import com.example.demo.domain.test.model.propagation.AnySeasonAndAllSubjectView;
import com.example.demo.domain.test.model.propagation.AnySeasonTestAverageView;
import com.example.demo.domain.test.model.propagation.OneSeasonTestAverageView;
import com.example.demo.domain.test.model.propagation.OneStudentView;
import com.example.demo.domain.test.model.propagation.UserTestFunctionView;
import com.example.demo.domain.test.model.propagation.UserTestView;

public interface UserTestRepository extends JpaRepository<UserTest, Long>, JpaSpecificationExecutor<UserTest> {

	/**
	 * 学年・年度・テスト時期情報から生徒ごとの全教科の合計点を算出し返却する
	 * 
	 * @param year     年度情報
	 * @param grade    学年
	 * @param season   テスト時期
	 * @param pageable ページング
	 * @return
	 */
	@Query("SELECT SUM(ut.point) AS sumPoint, au.firstName AS firstName, au.lastName AS lastName , ut.className AS clazzName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "INNER JOIN AppUser au ON ut.appUser = au.userId WHERE t.year = :year AND "
			+ "t.grade = :grade AND t.seasonName = :season GROUP BY au.userId")
	public Page<UserTestView> findAllUserTestView(@Param("year") int year, @Param("grade") byte grade,
			@Param("season") String season, Pageable pageable);

	/**
	 * 学年・クラス・年度・テスト時期情報から生徒ごとの全教科の合計点を算出し返却する
	 * 
	 * @param year      年度情報
	 * @param grade     学年
	 * @param className クラス名
	 * @param season    テスト時期
	 * @param pageable  ページング
	 * @return
	 */
	@Query("SELECT SUM(ut.point) AS sumPoint, au.firstName AS firstName, au.lastName AS lastName, ut.className AS clazzName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "INNER JOIN AppUser au ON ut.appUser = au.userId WHERE t.year = :year AND "
			+ "t.grade = :grade AND ut.className = :className And t.seasonName = :season GROUP BY au.userId")
	public Page<UserTestView> findAllUserTestView(@Param("year") int year, @Param("grade") byte grade,
			@Param("className") String className, @Param("season") String season, Pageable pageable);

	/**
	 * 年度・学年・テスト時期の情報から全クラスの生徒ごとの時期の全教科の合計点を算出し、合計点の最大値・最小値・平均を求め返却する
	 * 
	 * @param year   年度
	 * @param grade  学年
	 * @param season テスト時期
	 * @return
	 */
	@Query(nativeQuery = true, value = "select max(total) as maxPoint, min(total) as minPoint, avg(total) avgPoint from "
			+ "(select au.user_id, sum(ut.point) as total from user_test ut "
			+ "inner join test t on ut.test_id = t.test_id inner join app_user au on ut.user_id = au.user_id "
			+ "where t.year = :year and t.grade = :grade and t.season_name = :season group by au.user_id) num")
	public UserTestFunctionView findAllUserTestFunctionView(@Param("year") int year, @Param("grade") byte grade,
			@Param("season") String season);

	/**
	 * 年度・学年・テスト時期の情報から指定のクラスの生徒ごとの時期の全教科の合計点を算出し、合計点の最大値・最小値・平均を求め返却する
	 * 
	 * @param year          年度
	 * @param grade         学年
	 * @param classNameクラス名
	 * @param season        テスト時期
	 * @return
	 */
	@Query(nativeQuery = true, value = "select max(total) as maxPoint, min(total) as minPoint, avg(total) avgPoint from "
			+ "(select au.user_id, sum(ut.point) as total from user_test ut "
			+ "inner join test t on ut.test_id = t.test_id inner join app_user au on ut.user_id = au.user_id "
			+ "where t.year = :year and t.grade = :grade and ut.class_name = :className and t.season_name = :season group by au.user_id) num")
	public UserTestFunctionView findAllUserTestFunctionView(@Param("year") int year, @Param("grade") byte grade,
			@Param("className") String className, @Param("season") String season);

	/**
	 * 1つの学年の1クラスの生徒全体の教科ごとの平均点を算出し返却するメソッド
	 * 
	 * @param year      年度
	 * @param grade     学年
	 * @param className クラス名
	 * @param season    テスト時期
	 * @return
	 */
	@Query("SELECT ut.className AS clazzName, AVG(ut.point) AS avgPoint, t.subjectName AS subjectName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year = :year AND t.grade = :grade AND ut.className = :className AND t.seasonName = :season "
			+ "GROUP BY ut.className, t.subjectName")
	public List<OneSeasonTestAverageView> findOneSeasonTestAverage(@Param("year") int year, @Param("grade") byte grade,
			@Param("className") String className, @Param("season") String season);

	/**
	 * 1つの学年の全クラスの全生徒の教科ごとの平均点を算出し返却するメソッド
	 * 
	 * @param year   年度
	 * @param grade  学年
	 * @param season テスト時期
	 * @return
	 */
	// 学年全体の教科ごとの平均点 クラスオプションが単体・テスト時期が単体・教科が全て
	@Query("SELECT AVG(ut.point) AS avgPoint, t.subjectName AS subjectName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year = :year AND t.grade = :grade AND t.seasonName = :season GROUP BY t.subjectName")
	public List<OneSeasonTestAverageView> findOneSeasonTestAverage(@Param("year") int year, @Param("grade") byte grade,
			@Param("season") String season);

	/**
	 * 一つの学年の一つのクラスのテスト時期ごとの特定の教科の平均点を算出し返却するメソッド
	 * 
	 * @param year        年度
	 * @param grade       学年
	 * @param className   クラス名
	 * @param subjectName 教科名
	 * @param seasons     テスト時期
	 * @return
	 */
	@Query("SELECT AVG(ut.point) AS avgPoint, t.subjectName AS subjectName, ut.className AS clazzName, t.seasonName AS seasonName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year = :year AND t.grade = :grade AND ut.className = :className AND t.seasonName IN :seasons "
			+ "AND t.subjectName = :subjectName GROUP BY t.subjectName, t.seasonName ORDER BY t.seasonName")
	public List<AnySeasonTestAverageView> findAnySeasonTestAverage(@Param("year") int year, @Param("grade") byte grade,
			@Param("className") String className, @Param("subjectName") String subjectName,
			@Param("seasons") List<String> seasons);

	/**
	 * 一つの学年の全クラスのテスト時期ごとの特定の教科の平均点を算出し返却するメソッド
	 * 
	 * @param year        年度
	 * @param grade       学年
	 * @param subjectName 教科名
	 * @param seasons     テスト時期
	 * @return
	 */
	@Query("SELECT AVG(ut.point) AS avgPoint, t.subjectName AS subjectName, t.seasonName AS seasonName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year = :year AND t.grade = :grade AND t.seasonName IN :seasons "
			+ "AND t.subjectName = :subjectName GROUP BY t.subjectName, t.seasonName ORDER BY t.seasonName")
	public List<AnySeasonTestAverageView> findAnySeasonTestAverage(@Param("year") int year, @Param("grade") byte grade,
			@Param("subjectName") String subjectName, @Param("seasons") List<String> seasons);

	/**
	 * 一つの学年の一クラスの生徒のテスト時期ごとの全教科の合計点の平均値を算出し返却するメソッド
	 * 
	 * @param year      年度
	 * @param grade     学年
	 * @param className クラス名
	 * @param seasons   テスト時期
	 * @return
	 */
	@Query(value = "select season_name as seasonName, avg(total) as avgPoint from "
			+ "(select t.season_name, sum(point) total from user_test ut inner join test t on ut.test_id = t.test_id "
			+ "inner join app_user au on ut.user_id = au.user_id where t.year = :year and t.grade = :grade "
			+ "and ut.class_name = :className and t.season_name in(:seasons) "
			+ "group by t.season_name, au.user_id) as num group by season_name", nativeQuery = true)
	public List<AnySeasonAndAllSubjectView> findAnySeasonAndAllSubjects(@Param("year") int year,
			@Param("grade") byte grade, @Param("className") String className, @Param("seasons") List<String> seasons);

	/**
	 * 一つの学年の全クラスの生徒のテスト時期ごとの全教科の合計点の平均値を算出し返却するメソッド
	 * 
	 * @param year      年度
	 * @param grade     学年
	 * @param className クラス名
	 * @param seasons   テスト時期
	 * @return
	 */
	@Query(value = "select season_name as seasonName, avg(total) as avgPoint from "
			+ "(select t.season_name, sum(point) total from user_test ut inner join test t on ut.test_id = t.test_id "
			+ "inner join app_user au on ut.user_id = au.user_id where t.year = :year and t.grade = :grade "
			+ "and t.season_name in(:seasons) "
			+ "group by t.season_name, au.user_id) as num group by season_name", nativeQuery = true)
	public List<AnySeasonAndAllSubjectView> findAnySeasonAndAllSubjects(@Param("year") int year,
			@Param("grade") byte grade, @Param("seasons") List<String> seasons);

	/**
	 * 一つの学年の一つの教科のクラスごとのテスト時期ごとの平均点を算出し返却するメソッド
	 * 
	 * @param year        年度
	 * @param grade       学年
	 * @param subjectName 教科名
	 * @param seasons     テスト時期
	 * @return
	 */
	@Query("SELECT ut.className AS clazzName, AVG(ut.point) AS avgPoint, t.seasonName AS seasonName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year = :year AND t.subjectName = :subjectName AND t.grade = :grade AND t.seasonName IN :seasons "
			+ "GROUP BY ut.className, t.seasonName ORDER BY ut.className, t.seasonName")
	public List<AllClassOneSubjectView> findAllClassOneSubjects(@Param("year") int year, @Param("grade") byte grade,
			@Param("subjectName") String subjectName, @Param("seasons") List<String> seasons);

	// 全クラス・全教科・テスト時期が2以上
	/**
	 * 一つの学年のクラスごと生徒ごとの全教科の合計点の平均値を時期ごとに算出し返却
	 * 
	 * @param year    年度
	 * @param grade   学年
	 * @param seasons テスト時期
	 * @return
	 */
	@Query(value = "select class_name as clazzName, avg(total) as avgPoint, season_name as seasonName "
			+ "from (select au.user_id,ut.class_name, sum(point) as total, t.season_name from user_test ut "
			+ "inner join test t on ut.test_id = t.test_id inner join app_user au on ut.user_id = au.user_id "
			+ "where t.grade = :grade and t.year = :year and t.season_name in (:seasons) "
			+ "group by au.user_id, t.season_name, ut.class_name) as num "
			+ "group by class_name, season_name order by class_name, season_name", nativeQuery = true)
	public List<AllClassAndSubjectView> findAllClassAndSubjects(@Param("year") int year, @Param("grade") byte grade,
			@Param("seasons") List<String> seasons);

	/**
	 * 1つの学年の1りの生徒の教科ごとの得点を算出し返却するメソッド
	 * 
	 * @param grade  学年
	 * @param userId 学生ID
	 * @param season テスト時期
	 * @return
	 */
	@Query("SELECT t.subjectName AS subjectName, ut.className AS clazzName, ut.point AS userPoint, t.year AS year, "
			+ "au.firstName AS firstName, au.lastName AS lastName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId INNER JOIN AppUser au ON ut.appUser = au.userId "
			+ "WHERE t.grade = :grade AND au.userId = :userId AND t.seasonName = :season ORDER BY t.subjectName")
	public List<OneStudentView> findOneSeasonTestStudent(@Param("grade") byte grade, @Param("userId") Long userId,
			@Param("season") String season);

	/**
	 * 1つの学年の1りの生徒の全教科のテスト時期ごとの合計得点を算出し返却するメソッド
	 * 
	 * @param grade   学年
	 * @param userId  学生ID
	 * @param seasons テスト時期リスト
	 * @return
	 */
	@Query("SELECT ut.className AS clazzName, SUM(ut.point) AS userPoint, t.year AS year, "
			+ "au.firstName AS firstName, au.lastName AS lastName, t.seasonName AS seasonName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId INNER JOIN AppUser au ON ut.appUser = au.userId "
			+ "WHERE t.grade = :grade AND au.userId = :userId AND t.seasonName IN (:seasons) "
			+ "GROUP BY t.seasonName ORDER BY t.seasonName")
	public List<OneStudentView> findAnySeasonAllSubjectTestStudent(@Param("grade") byte grade,
			@Param("userId") Long userId, @Param("seasons") List<String> seasons);

	/**
	 * 1つの学年の1りの生徒の特定の教科のテスト時期ごとの得点を算出し返却するメソッド
	 * 
	 * @param grade       学年
	 * @param userId      学生ID
	 * @param subjectName 教科名
	 * @param seasons     テスト時期リスト
	 * @return
	 */
	@Query("SELECT ut.className AS clazzName, ut.point AS userPoint, t.year AS year, "
			+ "au.firstName AS firstName, au.lastName AS lastName, t.seasonName AS seasonName, t.subjectName AS subjectName "
			+ "FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId INNER JOIN AppUser au ON ut.appUser = au.userId "
			+ "WHERE t.grade = :grade AND au.userId = :userId AND t.seasonName IN (:seasons) AND t.subjectName = :subjectName "
			+ "ORDER BY t.seasonName")
	public List<OneStudentView> findAnySeasonOneSubjectTestStudent(@Param("grade") byte grade,
			@Param("userId") Long userId, @Param("subjectName") String subjectName,
			@Param("seasons") List<String> seasons);

	/**
	 * 年度・学年・テスト時期・教科名の情報からテスト点数を抽出し返却する
	 * 
	 * @param year        年度
	 * @param grade       学年
	 * @param season      テスト時期
	 * @param subjectName 教科名
	 * @return
	 */
	@Query("SELECT ut.point FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year = :year AND t.grade = :grade AND t.seasonName = :season AND t.subjectName = :subjectName")
	public List<Integer> findAllPoints(@Param("year") Integer year, @Param("grade") byte grade,
			@Param("season") String season, @Param("subjectName") String subjectName);

	/**
	 * ユーザーID・学年・テスト時期・教科名を元に年度情報を取得し返却
	 * 
	 * @param studentId   一意のユーザーID
	 * @param grade       学年
	 * @param season      テスト時期
	 * @param subjectName 教科名
	 * @return
	 */
	@Query("SELECT t.year FROM UserTest ut  INNER JOIN Test t ON ut.test = t.testId "
			+ "INNER JOIN AppUser au ON ut.appUser = au.userId WHERE au.userId =:studentId "
			+ "AND t.grade =:grade AND t.seasonName =:season AND t.subjectName =:subjectName ")
	public Integer findYearOfTestByStudentInfo(@Param("studentId") Long studentId, @Param("grade") byte grade,
			@Param("season") String season, @Param("subjectName") String subjectName);

	/**
	 * 年度情報を元にその年度の登録されているテスト結果の時期情報を取得し返却する
	 * 
	 * @param year 年度
	 * @return
	 */
	@Query("SELECT t.seasonName FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year =:year GROUP BY t.seasonName " + "ORDER BY t.seasonName DESC")
	public List<String> findAllSeasonByYear(@Param("year") int year);

	/**
	 * 年度情報を元にその年度の登録されているテスト結果の時期情報を取得し返却する
	 * 
	 * @param year      年度
	 * @param grade     学年
	 * @param className クラス名
	 * @return
	 */
	@Query("SELECT t.seasonName FROM UserTest ut INNER JOIN Test t ON ut.test = t.testId "
			+ "WHERE t.year =:year AND t.grade =:grade AND ut.className =:className "
			+ "GROUP BY t.seasonName ORDER BY t.seasonName DESC")
	public List<String> findAllSeasonByYear(@Param("year") int year, @Param("grade") byte grade,
			@Param("className") String className);
}
