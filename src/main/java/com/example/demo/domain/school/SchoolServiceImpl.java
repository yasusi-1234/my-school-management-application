package com.example.demo.domain.school;

import static com.example.demo.common.SchoolCommonValue.BR;
import static com.example.demo.common.SchoolCommonValue.CLASS_NAME;
import static com.example.demo.common.SchoolCommonValue.FIRST_NAME;
import static com.example.demo.common.SchoolCommonValue.GRADE;
import static com.example.demo.common.SchoolCommonValue.LAST_NAME;
import static com.example.demo.common.SchoolCommonValue.PASSWORD;
import static com.example.demo.common.SchoolCommonValue.POINT;
import static com.example.demo.common.SchoolCommonValue.ROLE_STUDENT;
import static com.example.demo.common.SchoolCommonValue.SEASON_NAME;
import static com.example.demo.common.SchoolCommonValue.SUBJECT_NAME;
import static com.example.demo.common.SchoolCommonValue.USER_ID;
import static com.example.demo.common.SchoolCommonValue.USER_NAME;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;
import com.example.demo.controller.form.GradeClassForm;
import com.example.demo.controller.form.SchoolDataForm;
import com.example.demo.controller.form.SurveyRecordForm;
import com.example.demo.domain.appuser.model.AppUser;
import com.example.demo.domain.appuser.model.Role;
import com.example.demo.domain.appuser.repository.AppUserRepository;
import com.example.demo.domain.appuser.repository.RoleRepository;
import com.example.demo.domain.appuser.service.UserHelper;
import com.example.demo.domain.excel.ExcelInformationReader;
import com.example.demo.domain.excel.NecessaryInformation;
import com.example.demo.domain.grade.model.GradeClass;
import com.example.demo.domain.grade.model.UserGradeClass;
import com.example.demo.domain.grade.model.propagation.UserGradeClassView;
import com.example.demo.domain.grade.repository.GradeClassRepository;
import com.example.demo.domain.grade.repository.UserGradeClassRepository;
import com.example.demo.domain.grade.service.GradeClassHelper;
import com.example.demo.domain.grade.service.UserGradeClassHelper;
import com.example.demo.domain.school.exception.AlreadyRegisterDataException;
import com.example.demo.domain.school.exception.AppUserNotRegisterException;
import com.example.demo.domain.school.exception.ExcelDataException;
import com.example.demo.domain.school.exception.GradeClassNotRegisterException;
import com.example.demo.domain.school.exception.TestInformationNotRegisterException;
import com.example.demo.domain.test.model.Test;
import com.example.demo.domain.test.model.UserTest;
import com.example.demo.domain.test.model.propagation.UserTestFunctionView;
import com.example.demo.domain.test.model.propagation.UserTestView;
import com.example.demo.domain.test.repository.TestRepository;
import com.example.demo.domain.test.repository.UserTestRepository;
import com.example.demo.domain.test.service.helper.TestHelper;
import com.example.demo.domain.test.service.helper.UserTestHelper;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SchoolServiceImpl implements SchoolService {

	private final ExcelInformationReader excelInformationReader;

	private final GradeClassRepository gradeClassRepository;

	private final RoleRepository roleRepository;

	private final AppUserRepository userRepository;

	private final UserGradeClassRepository userGradeClassRepository;

	private final TestRepository testRepository;

	private final UserTestRepository userTestRepository;

	private final PasswordEncoder passwordEncoder;

	/**
	 * 学生とそれに対応する学年クラスとの中間テーブルの新規挿入用メソッド
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<UserGradeClass> insertAllGradeClass(SchoolDataForm form) {
		List<Map<String, String>> informationListMap = createResultList(form);
//		log.info(informationListMap.toString());
		// 学年情報が複数選択されていないかチェック
		checkExcelDataMap(informationListMap);
		// マップを一つ抽出
		Map<String, String> firstElementMap = informationListMap.get(0);
		// 年度情報
		int year = NumberUtils.toInt(firstElementMap.get(YEAR));
		// 学年情報
		int grade = NumberUtils.toInt(firstElementMap.get(GRADE));

		// 既に登録済みで無いかのチェック
		allReadyRegisterStudentCheck(informationListMap, year);

		// リクエストされたクラス情報セット
		List<String> classNames = informationListMap.stream().map(item -> item.get(CLASS_NAME)).distinct().collect(Collectors.toList());
		// 学年クラステーブル作成 ※すでに存在した場合は何もしない設計になっている
		List<GradeClass> gradeClassList = createGradeClassInformation(year, grade, classNames);
		// テスト情報も作成
		createTestInformationOfYear(year);

		// 学生用ロール情報取得
		Role role = roleRepository.findByRoleName(ROLE_STUDENT);
		// 保存用学生リスト(ID無し)
		List<AppUser> students = new ArrayList<>();
		// 保存用学生リストに対応した学年クラス情報
		List<GradeClass> gradeClasses = new ArrayList<>();
		log.info("create save list");
		// 保存用リストに要素を格納
		String password = passwordEncoder.encode(PASSWORD); // エンコードしたパスワード
		for (Map<String, String> map : informationListMap) {
			AppUser user = AppUser.of(map.get(USER_NAME), password, map.get(FIRST_NAME), map.get(LAST_NAME), role);
			students.add(user);
			for (GradeClass gc : gradeClassList) {
				if (Objects.equal(gc.getClassName(), map.get(CLASS_NAME))) {
					// 学年とクラス名が等しければ
					gradeClasses.add(gc);
					break;
				}
			}
		}
		log.info("before student save all");
		// DBに保存された学生情報リスト(ID持ち)
		List<AppUser> createdStudents = userRepository.saveAll(students);
		// 保存用ユーザー(学生と学年クラス)の中間テーブルリスト
		List<UserGradeClass> userGradeClasses = new ArrayList<>();

		log.info("appUsersSize: {}, gradeClassesSize: {}", createdStudents.size(), gradeClasses.size());
		for (int i = 0; i < createdStudents.size(); i++) {
			UserGradeClass ugc = new UserGradeClass();
			ugc.setAppUser(createdStudents.get(i));
			ugc.setGradeClass(gradeClasses.get(i));
			userGradeClasses.add(ugc);
		}

		return userGradeClassRepository.saveAll(userGradeClasses);
	}

	/**
	 * リクエストデータの学年情報が複数無いか調査する
	 * @param requestListMap リクエストされたデータ
	 * @throws ExcelDataException 学年情報が複数選択されている場合
	 */
	private void checkExcelDataMap(List<Map<String, String>> requestListMap){
		List<String> grades = requestListMap.stream().map(item -> item.get(GRADE))
				.distinct().collect(Collectors.toList());
		if (grades.size() > 1){
			throw new ExcelDataException("学年情報(grade)が複数見つかりました。学年情報は単一の値で選択してください");
		}
	}

	/**
	 * 学生に対応する学年クラス中間テーブルの新規挿入用、主に昇級した際に使われる想定
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<UserGradeClass> updateAllGradeClasses(SchoolDataForm form) {
		List<Map<String, String>> informationListMap = createResultList(form);
//		log.info(informationListMap.toString());
		// マップを一つ抽出
		Map<String, String> firstElementMap = informationListMap.get(0);
		// リクエストされた更新年度情報
		int requestYear = NumberUtils.toInt(firstElementMap.get(YEAR));
		// リクエストされた前年度の情報
		int beforeOneYear = requestYear - 1;
		// リクエストされた学年情報
		int grade = NumberUtils.toInt(firstElementMap.get(GRADE));
		// リクエストされた学年情報の1年前の学年
		int beforeGrade = grade - 1;

		// 実在する学年クラス情報(year年度の)
		List<GradeClass> gradeClassList = gradeClassRepository.findByYear(requestYear);

		// 1年前の学年クラス・学生の中間テーブルを取得
		List<UserGradeClass> userGradeClassesOfOneYearAgo =
				findAllByYearAndGrade(beforeOneYear, beforeGrade);

		// 1年前の中間テーブル走査
		boolean userExistsOneYearAgo = checkRequestAndDBInformation(informationListMap, userGradeClassesOfOneYearAgo);

		// リクエスト年の学年クラス・学生の中間テーブルの取得
		List<UserGradeClass> userGradeClassesOfRequestYear =
				findAllByYearAndGrade(requestYear, grade);

		// リクエスト年の中間テーブル走査
		boolean userExistsRequestYear = checkRequestAndDBInformation(informationListMap, userGradeClassesOfRequestYear);

		if (userExistsOneYearAgo){
			// リクエスト年の1年前のデータに一致するユーザー情報が存在した
			if(userExistsRequestYear){
				// リクエスト年のデータに一致するユーザーが存在した ※リクエスト年の更新
				return updateOfUserGradeClass(requestYear, grade, informationListMap, userGradeClassesOfRequestYear);
			}else{
				// リクエスト年のデータに一致するユーザーが存在しなかった ※リクエスト年に新規挿入
				return createOfUserGradeClass(requestYear, grade, informationListMap, userGradeClassesOfOneYearAgo);
			}
		}else{
			// リクエスト年の1年前のデータに一致するユーザー情報が存在しなかった
			if(userExistsRequestYear){
				// リクエスト年のデータに一致するユーザーが存在した ※リクエスト年の更新
				return updateOfUserGradeClass(requestYear, grade, informationListMap, userGradeClassesOfRequestYear);
			}else{
				// リクエスト年のデータに一致するユーザーが存在しなかった ※例外
				throw new AppUserNotRegisterException("更新するためのユーザー情報が登録されていません。");
			}
		}

	}

	/**
	 * 引数の情報を元に学年・クラス情報とユーザー情報の中間テーブルを更新するメソッド
	 * @param year 年度
	 * @param grade 学年
	 * @param requestListMap リクエストされた学年クラス情報のListMap
	 * @param dbUserGradeClasses 学年クラス・ユーザー情報の中間テーブル
	 * @return 更新された中間テーブル情報
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	private List<UserGradeClass> updateOfUserGradeClass(
			int year,
			int grade,
			List<Map<String, String>> requestListMap,
			List<UserGradeClass> dbUserGradeClasses
	){
		List<GradeClass> gradeClasses = findAllGradeClassByYearAndGrade(year, grade);

		for (UserGradeClass ugc: dbUserGradeClasses){
			for (Map<String, String> userMap: requestListMap){
				if(Objects.equal(ugc.getAppUser().getUserId(), NumberUtils.toLong(userMap.get(USER_ID)))){
					if(Objects.equal(ugc.getGradeClass().getClassName(),userMap.get(CLASS_NAME))){
						// クラス名が一致していたら何もしない
						break;
					}
					GradeClass newGradeClass = gradeClasses.stream()
							.filter(gc -> Objects.equal(userMap.get(CLASS_NAME), gc.getClassName()))
									.findFirst().orElseThrow(() -> new GradeClassNotRegisterException("登録されていないクラス名は利用できません。リクエストされたクラス名: " + userMap.get(CLASS_NAME)));
					ugc.setGradeClass(newGradeClass);
					break;
				}
			}
		}

		return userGradeClassRepository.saveAll(dbUserGradeClasses);
	}

	/**
	 * 新たに学年クラス情報とユーザー情報の中間テーブルを作成し挿入するメソッド
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @param requestListMap リクエストされた学年・クラス・ユーザー情報
	 * @param dbUserGradeClasses リクエストの１年前の学年クラス・ユーザーの中間テーブル情報
	 * @return 新規挿入された中間テーブル情報
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	private List<UserGradeClass> createOfUserGradeClass(
			int year,
			int grade,
			List<Map<String, String>> requestListMap,
			List<UserGradeClass> dbUserGradeClasses // リクエストの１年前の情報
	){
		List<String> classNames = requestListMap.stream().map(m -> m.get(CLASS_NAME)).distinct().collect(Collectors.toList());
		// DBに登録されている OR 新規に登録された学年クラス情報(リクエスト年の)
		List<GradeClass> gradeClasses = createGradeClassInformation(year, grade, classNames);

		List<UserGradeClass> insertUserGradeClass = new ArrayList<>();

		for(Map<String, String> map: requestListMap){
			Long userId = NumberUtils.toLong(map.get(USER_ID));
			GradeClass gc = gradeClasses.stream().filter(item -> Objects.equal(item.getClassName(),map.get(CLASS_NAME))).findFirst().get();
			for(UserGradeClass ugc: dbUserGradeClasses){
				if(Objects.equal(userId, ugc.getAppUser().getUserId())){
					UserGradeClass userGradeClass = new UserGradeClass();
					userGradeClass.setAppUser(ugc.getAppUser());
					userGradeClass.setGradeClass(gc);
					insertUserGradeClass.add(userGradeClass);
					break;
				}
			}
		}
		List<UserGradeClass> saveResult = userGradeClassRepository.saveAll(insertUserGradeClass);
		/* テスト情報も登録 */
		createTestInformationOfYear(year);
		return saveResult;
	}

	/**
	 * リクエストされた情報とデータベースの情報を走査し、一致する情報があるか確認する
	 * @param informationListMap リクエストされた情報
	 * @param userGradeClasses データベースの情報
	 * @return 一致する情報が1つ見つかった: true 一致する情報が1つも見つからなかった: false
	 */
	private boolean checkRequestAndDBInformation(
			List<Map<String, String>> informationListMap,
			List<UserGradeClass> userGradeClasses
	){
		if (userGradeClasses.isEmpty()){
			// DBの要素が空の場合
			return false;
		}else{
			// DBの要素が空でない場合
			boolean findUser = false;
			for (int i = 0; i < userGradeClasses.size(); i++) {
				AppUser user = userGradeClasses.get(i).getAppUser();
				for(Map<String, String> requestMap: informationListMap){
					Long userIdOfDB = user.getUserId();
					Long userIdOfRequest = NumberUtils.toLong(requestMap.get(USER_ID));
					if (Objects.equal(userIdOfDB, userIdOfRequest)){
						findUser = true;
						break;
					}
				}

				if(findUser){
					break;
				}
			}
			// 1つでも一致する要素が見つかればtrueを返却する
			return findUser;
		}

	}

	/**
	 * 既に登録済みのユーザーがDBに存在するかチェックするメソッド
	 * 
	 * @param informationList リクエストされたデータリスト
	 * @param year            リクエストされた年度情報
	 * @throws AlreadyRegisterDataException リクエストされたデータの年度のユーザー情報とDBに登録されている情報に一致した
	 *                                      ユーザー情報が格納されていた場合に送出される
	 */
	private void allReadyRegisterStudentCheck(List<Map<String, String>> informationList, int year) {
		log.info("{}", year);
		List<UserGradeClass> dbUserGradeClass = userGradeClassRepository.findAll(
				Specification.where(UserGradeClassHelper.fetchUser()).and(UserGradeClassHelper.equalYear(year)));

		if (CollectionUtils.isEmpty(dbUserGradeClass)) {
			// 値が格納されていないならば
			return;
		}

		int alreadyRegisterCount = 0;
		for (UserGradeClass ugc : dbUserGradeClass) {
			for (Map<String, String> map : informationList) {
				if (Objects.equal(ugc.getAppUser().getUserName(), map.get(USER_NAME))) {
					// 既に登録されているUserNameの値が等しい
					alreadyRegisterCount++;
					break;
				}
			}
		}
		String format = String.format("既に登録されているユーザー情報が見つかりました。リクエスト数: %d 件, DB登録数: %d, 同一件数: %d, 処理を実行しませんでした。",
				informationList.size(), dbUserGradeClass.size(), alreadyRegisterCount);
		if (alreadyRegisterCount > 0) {
			throw new AlreadyRegisterDataException(format);
		}
	}

	/**
	 * テスト結果の情報を新規に挿入するメソッド
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<UserTest> insertAllStudentTest(SchoolDataForm form) {
		List<Map<String, String>> informationListMap = createResultList(form);
//		log.info(informationListMap.toString());
		// マップを一つ抽出
		Map<String, String> firstElementMap = informationListMap.get(0);
		// 年度情報
		int year = NumberUtils.toInt(firstElementMap.get(YEAR));
		// 既に登録されているかのチェック
		alreadyRegisterTestCheck(firstElementMap, year);

		List<UserTest> createUserTests = createUserTestList(informationListMap);

//		log.info(createUserTests.toString());
		return userTestRepository.saveAll(createUserTests);
	}

	/**
	 * 登録リクエストのテストデータがすでに存在していた場合に、例外を投げるためのチェッククラス
	 * ※注意※現在の実装では単純な実装の為、今後要件に合わせて実装しなおす必要があるかもしれない
	 * 
	 * @param firstElementMap リクエストデータから取得した1要素のMap
	 * @param year 年度情報
	 */
	private void alreadyRegisterTestCheck(Map<String, String> firstElementMap, int year) {
		String className = firstElementMap.get(CLASS_NAME);
		int grade = NumberUtils.toInt(firstElementMap.get(GRADE));
		String seasonName = firstElementMap.get(SEASON_NAME);
		String subjectName = firstElementMap.get(SUBJECT_NAME);
		List<UserTest> dbResult = userTestRepository.findAll(Specification.where(UserTestHelper.equalYear(year))
				.and(UserTestHelper.equalGrade(grade)).and(UserTestHelper.equalClass(className))
				.and(UserTestHelper.equalSeasonName(seasonName)).and(UserTestHelper.equalSubjectName(subjectName)));
		log.info("check dbResult size: {}", dbResult.size());
		if (dbResult.size() > 0) {
			throw new AlreadyRegisterDataException("既に登録されているテスト情報が見つかったため、処理をキャンセルしました。");
		}
	}

	/**
	 * Excelから作成されたListMap情報を元に新規登録用のリストを作成し返却する
	 * 
	 * @param informationList リクエストデータから取得したListMap
	 * @return 新規登録用のUserTestリスト
	 * @throws TestInformationNotRegisterException リクエストされたテストのデータの年度情報・テスト時期情報
	 *                                             がDBにまだ格納されていない場合に送出される
	 * @throws ExcelDataException                  リクエストされたDataの教科名・テスト時期・学年の値が異常と判断された
	 *                                             場合に送出される
	 */
	private List<UserTest> createUserTestList(List<Map<String, String>> informationList) {
		Map<String, String> oneInfo = informationList.get(0);
		int year = NumberUtils.toInt(oneInfo.get(YEAR));
		String seasonName = oneInfo.get(SEASON_NAME);
		// 年度・テスト時期情報から抽出したテスト情報
		List<Test> dbTests = testRepository
				.findAll(Specification.where(TestHelper.equalYear(year)).and(TestHelper.equalSeason(seasonName)));
		if (dbTests.isEmpty()) {
			createTestInformationOfYear(year);
//			throw new TestInformationNotRegisterException(year + "年度の" + seasonName + "は現在DBに登録されていません。確認後登録してください。");
		}
		// 5件の学生ID
		List<Long> userIds = informationList.stream().limit(5).map(m -> NumberUtils.toLong(m.get(USER_ID)))
				.collect(Collectors.toList());
		List<AppUser> users = userRepository.findAll(UserHelper.inUserId(userIds));
		if (userIds.size() != users.size()) {
			// 実際に登録されているユーザー情報を、リクエストされたユーザー情報の数が一致しない(試験用の為要素は5つで判断している)
			throw new AppUserNotRegisterException("リクエストされた学生情報が見つかりません。学生情報がDBに登録されているか確認してください。");
		}

		List<UserTest> createList = new ArrayList<>();
		Role role = roleRepository.findByRoleName(ROLE_STUDENT);
		for (int i = 0; i < informationList.size(); i++) {
			Map<String, String> map = informationList.get(i);

			AppUser user = AppUser.of(NumberUtils.toLong(map.get(USER_ID)), role);
			// 学年・テスト時期・教科情報が一致したTest情報
			Test test = dbTests.stream()
					.filter(t -> Objects.equal(t.getSeasonName(), map.get(SEASON_NAME))
							&& Objects.equal(t.getSubjectName(), map.get(SUBJECT_NAME))
							&& Objects.equal(t.getGrade(), NumberUtils.toInt(map.get(GRADE))))
					.findFirst().orElse(null);
			if (test == null) {
				throw new ExcelDataException("Excel row : " + i + ", 時期： " + map.get(SEASON_NAME) + ", 教科： "
						+ map.get(SUBJECT_NAME) + ", 学年: " + map.get(GRADE) + BR + "の情報に記述間違いが無いか確認してください");
			}

			UserTest userTest = UserTest.of(map.get(CLASS_NAME), NumberUtils.toInt(map.get(POINT)), user, test);
			createList.add(userTest);
		}
		return createList;
	}

	/**
	 * Excel情報を元に既に登録されているUserTest情報を更新するメソッド
	 * ※注意※使われる想定では点数の修正情報のみ、また年度・時期・教科名は共通の値が格納されている想定
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<UserTest> updateAllStudentTest(SchoolDataForm form) {
		List<Map<String, String>> informationList = createResultList(form);
		Map<String, String> oneInfo = informationList.get(0);
		int year = NumberUtils.toInt(oneInfo.get(YEAR)); // 年度
		String seasonName = oneInfo.get(SEASON_NAME); // 時期
		String subjectName = oneInfo.get(SUBJECT_NAME); // 教科名
		List<Integer> grades = informationList.stream().map(m -> NumberUtils.toInt(m.get(GRADE))).distinct()
				.collect(Collectors.toList());

		// リクエストされたデータと一致する情報をDBから取得
		List<UserTest> dbUserTests = userTestRepository.findAll(
				Specification.where(UserTestHelper.equalYear(year)).and(UserTestHelper.equalSeasonName(seasonName))
						.and(UserTestHelper.equalSubjectName(subjectName)).and(UserTestHelper.inGrade(grades)));
		log.info("requestExcelList Size: {}, dbUserTests Size: {}", informationList.size(), dbUserTests.size());
		if (informationList.size() != dbUserTests.size()) {
			// リクエストの数と実際のDbに登録されている数が合わない
			throw new ExcelDataException("リクエストされたExcelの情報数とDBに登録されている情報の数が合いません。データに間違いが無いか確認してください");
		}

//		log.info("beforeUserTests: " + dbUserTests.toString());

		for (UserTest userTest : dbUserTests) {
			for (Map<String, String> map : informationList) {
				if (Objects.equal(userTest.getAppUser().getUserId(), NumberUtils.toLong(map.get(USER_ID)))) {
					// ユーザーID(一意のID)が一致した
					int requestPoint = NumberUtils.toInt(map.get(POINT));
					if (Objects.equal(userTest.getPoint(), requestPoint)) {
						// 点数が等しい場合は何もせずbreak
						break;
					}
					userTest.setPoint(requestPoint);
					break;
				}
			}
		}
//		log.info("afterUserTests: " + dbUserTests.toString());
		return userTestRepository.saveAll(dbUserTests);
	}

	/**
	 * 学年・クラス情報を新規に登録するメソッド
	 * @param form 作成したい年度・学園・クラス情報が入った情報
	 * @return 新たに挿入された学年・クラス情報のリスト
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<GradeClass> createGradeClassInformation(GradeClassForm form) {
		// リクエストに選択された情報から現在DBに登録されている学年・クラス情報を抽出(抽出元情報：学年・クラスリスト・年度)
		List<GradeClass> dbClasses = gradeClassRepository
				.findAll(Specification.where(GradeClassHelper.equalYear(form.getYear()))
						.and(GradeClassHelper.equalGrade(form.getGrade().getGrade()))
						.and(GradeClassHelper.equalClassNames(form.getClassList())));
		// リクエストに選択された情報から作成した学年・クラス情報を抽出
		List<GradeClass> gradeClasses = new ArrayList<>();

		if (dbClasses.isEmpty()) {
			// かぶっている要素が無い
			for (int i = 0; i < form.getClassList().size(); i++) {
				GradeClass gc = new GradeClass();
				gc.setClassName(form.getClassList().get(i).name());
				gc.setGrade(form.getGrade().getGrade());
				gc.setYear(form.getYear());
				gradeClasses.add(gc);
			}
		} else {
			// かぶっている要素がある
			List<String> classNames = dbClasses.stream().map(GradeClass::getClassName).collect(Collectors.toList());
			for (int i = 0; i < form.getClassList().size(); i++) {
				if (classNames.contains(form.getClassList().get(i).name())) {
					continue;
				}
				GradeClass gc = new GradeClass();
				gc.setClassName(form.getClassList().get(i).name());
				gc.setGrade(form.getGrade().getGrade());
				gc.setYear(form.getYear());
				gradeClasses.add(gc);
			}
		}

		log.info(dbClasses.toString());
		log.info(gradeClasses.toString());
		return gradeClassRepository.saveAll(gradeClasses);
	}

	/**
	 * 学年・クラス情報を新規に登録するメソッド(一つの学年の情報が選択される想定)
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @param classNames リスト型のクラス情報
	 * @return 情報を元に新規に登録(挿入)された学年情報 ※指定の年度と指定の学年に既に存在する学年・クラス情報も合わせて返却される
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<GradeClass> createGradeClassInformation(int year, int grade, List<String> classNames){
		// リクエストに選択された情報から現在DBに登録されている学年・クラス情報を抽出(抽出元情報：学年・クラスリスト・年度)
		List<GradeClass> dbClasses = gradeClassRepository
				.findAll(Specification.where(GradeClassHelper.equalYear(year))
						.and(GradeClassHelper.equalGrade(grade))
						.and(GradeClassHelper.equalStringClassNames(classNames)));
		// リクエストに選択された情報から作成した学年・クラス情報を抽出
		if (dbClasses.isEmpty()){
			// 被っているいる情報が無い
			List<GradeClass> gradeClasses = new ArrayList<>();

			for (String className: classNames){
				GradeClass gc = GradeClass.of(grade, className, year);
				gradeClasses.add(gc);
			}
			log.info("add gradeClasses: {}", gradeClasses);
			return gradeClassRepository.saveAll(gradeClasses);
		}else {
			// 被っている情報がある
			// DB内に存在したクラス名リスト
			List<String> dbClassNames = dbClasses.stream().map(GradeClass::getClassName).collect(Collectors.toList());
			for (String className: classNames){
				if(dbClassNames.contains(className)){
					continue;
				}
				GradeClass gc = GradeClass.of(grade, className, year);
				dbClasses.add(gc);
			}
			log.info("add gradeClasses: {}", dbClasses);
			return gradeClassRepository.saveAll(dbClasses);
		}
	}

	/**
	 * 指定された年度のテスト情報を新たに挿入するメソッド 既にDBに登録されている学年・クラス情報(GradeClassテーブル)を元に作成する
	 * @param year 年度情報
	 * @return 年度情報を元に新規に登録(挿入)されたTest情報リスト
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public List<Test> createTestInformationOfYear(int year) {
		// 指定された年度の学年クラスリスト
		List<GradeClass> dbGradeClasses = gradeClassRepository.findAll(GradeClassHelper.equalYear(year));
		// 登録されていた学年を絞り込み
		List<Integer> grades = dbGradeClasses.stream().map(GradeClass::getGrade).distinct().collect(Collectors.toList());
		if (dbGradeClasses.isEmpty()) {
			// DBに指定された年度の学年クラスが存在しない場合
			return new ArrayList<>();
		}
		// dbに登録されているyear年度のテスト
		List<Test> dbTests = testRepository.findAll(TestHelper.equalYear(year));
		// dbに新規登録するTest
		List<Test> createTests = new ArrayList<>();

		for (Integer grade : grades) {
			// dbに登録されているテスト情報と学年が一致するものがある
			boolean contain = dbTests.stream().anyMatch(test -> Objects.equal(test.getGrade(), grade));
			if (contain) {
				continue;
			}
			for (Season season : Season.values()) {
				for (Subject subject : Subject.ALL.valuesExcludeAll()) {
					Test test = new Test();
					test.setGrade(grade);
					test.setYear(year);
					test.setSeasonName(season.getSeasonName());
					test.setSubjectName(subject.getSubjectName());
					createTests.add(test);
				}
			}
		}

		return testRepository.saveAll(createTests);
	}

	/**
	 * ユーザーテストID(一意)の情報と新しい点数の情報を元に、テスト情報を更新する
	 * @param userTestId ユーザーのテストID
	 * @param newPoint 新規の点数
	 * @return 更新されたユーザーのテスト情報
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public UserTest singleUpdateUserTest(Long userTestId, int newPoint) {
		UserTest target = userTestRepository.findById(userTestId)
				.orElseThrow(() -> new TestInformationNotRegisterException(
						"リクエストされたUSER TEST ID: " + userTestId + ", のデータが見つかりませんでした。" + "処理をキャンセルしました"));
		// 例外処理
		target.setPoint(newPoint);
		return userTestRepository.save(target);
	}

	/**
	 * Excelから必要な情報を推測して、要求に応じたListMapを返却する
	 * 
	 * @param form リクエスト情報が入ったフォーム
	 * @return リクエスト情報を元に、添付のExcel情報から作成したListMap
	 */
	private List<Map<String, String>> createResultList(SchoolDataForm form) {
		NecessaryInformation info = excelInformationReader.getNecessaryInformation(form.getRegistrationData(),
				form.getDbInjectType());
		List<Map<String, String>> resultList = null;
		try (InputStream in = form.getMultipartFile().getInputStream();) {
			resultList = excelInformationReader.analyzeExcelFile(in, info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	// *************************************** 検索系 ******************************************

	/**
	 * 年度・学年・クラス・テスト時期・教科情報などの様々な情報を元に必要な情報を検索し、テスト情報を返却するメソッド
	 *
	 */
	@Override
	public Page<UserTest> findAllRequestTestInformation(SurveyRecordForm form, Pageable pageable) {
		return userTestRepository.findAll(Specification.where(UserTestHelper.fetchUserAndTest()
				.and(UserTestHelper.equalYear(form.getYear())).and(UserTestHelper.equalGrade(form.getGrade()))
				.and(UserTestHelper.equalClass(form.getClazz())).and(UserTestHelper.equalSeasons(form.getSeason()))
				.and(UserTestHelper.equalSubject(form.getSubject()))), pageable);
	}

	/**
	 * ユーザーID・学年・クラス・テスト時期・教科情報を元に1人のテスト情報を検索し返却するメソッド
	 * @param studentId ユーザーID(学生)
	 * @param grade 学年情報
	 * @param subject 教科情報
	 * @param seasons 時期情報
	 * @return 引数で得た情報を元に取得したテスト情報リスト(テストとユーザーの中間テーブル)
	 */
	@Override
	public List<UserTest> findAllSingleStudentTests(Long studentId, Grade grade, Subject subject,
			List<Season> seasons) {
		return userTestRepository.findAll(Specification.where(UserTestHelper.fetchUserAndTest())
				.and(UserTestHelper.equalAppUserId(studentId)).and(UserTestHelper.equalGrade(grade))
				.and(UserTestHelper.equalSubject(subject)).and(UserTestHelper.equalSeasons(seasons)));
	}

	/**
	 * ユーザーテストIDの情報を元に1件のUserTest情報を抽出する
	 * @param studentId ユーザー(学生)のID
	 * @return ユーザーテストIDの情報を元に取得した1件の情報(テストとユーザーの中間テーブル)
	 */
	@Override
	public UserTest findByUserTestId(Long studentId) {
		return userTestRepository.findOne(
				Specification.where(UserTestHelper.fetchUserAndTest()).and(UserTestHelper.equalUserTestId(studentId)))
				.orElse(null);
	}

	/**
	 * 年度・学年・クラス・氏名情報を元にユーザー(学生)情報を検索する
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @param firstName ユーザーの名前
	 * @param lastName ユーザーの苗字
	 * @param clazz クラス情報
	 * @param pageable ページング情報
	 * @return 各情報を元に得た、ユーザーと学年情報の中間テーブル情報(Page)
	 */
	@Override
	public Page<UserGradeClass> findSearchGradeClass(int year, Grade grade, Clazz clazz, String firstName,
			String lastName, Pageable pageable) {
		return userGradeClassRepository.findAll(Specification.where(UserGradeClassHelper.fetchAll())
				.and(UserGradeClassHelper.equalYear(year)).and(UserGradeClassHelper.equalGrade(grade))
				.and(UserGradeClassHelper.equalClass(clazz)).and(UserGradeClassHelper.likeFirstName(firstName))
				.and(UserGradeClassHelper.likeLastName(lastName)).and(UserGradeClassHelper.equalRole(ROLE_STUDENT)),
				pageable);
	}

	/**
	 * 年度・学年・クラス情報を元に情報を抽出して返却するメソッド
	 */
	@Override
	public List<UserGradeClass> findSearchGradeClass(int year, Grade grade, Clazz clazz) {
		return userGradeClassRepository.findAll(Specification.where(UserGradeClassHelper.fetchAll())
				.and(UserGradeClassHelper.equalYear(year)).and(UserGradeClassHelper.equalGrade(grade))
				.and(UserGradeClassHelper.equalClass(clazz)).and(UserGradeClassHelper.equalRole(ROLE_STUDENT)));
	}

	/**
	 * 年度・学年・時期・ページ数(Order情報込み)から個人のテスト時期の合計点を算出し返却するメソッド
	 * 
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @param season 時期
	 * @param pageable ページング情報
	 */
	@Override
	public Page<UserTestView> findAllTestUserView(int year, int grade, String season, Pageable pageable) {
		return userTestRepository.findAllUserTestView(year, grade, season, pageable);
	}

	/**
	 * 年度・学年・クラス・時期・ページ数(Order情報込み)から個人のテスト時期の合計点を算出し返却するメソッド
	 * 
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @param className クラス名
	 * @param season 時期情報
	 * @param pageable ページング情報
	 */
	@Override
	public Page<UserTestView> findAllTestUserView(int year, int grade, String className, String season,
			Pageable pageable) {
		return userTestRepository.findAllUserTestView(year, grade, className, season, pageable);
	}

	/**
	 * 年度・学年・テスト時期の情報から生徒ごとの合計点を算出し、合計点の最大値・平均値・最小値を計算したデータを返却する
	 * 
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @param season 時期情報
	 */
	@Override
	public UserTestFunctionView findAllTestUserFunctionView(int year, int grade, String season) {
		return userTestRepository.findAllUserTestFunctionView(year, grade, season);
	}

	/**
	 * 年度・学年・クラス・テスト時期の情報から生徒ごとの合計点を算出し、合計点の最大値・平均値・最小値を計算したデータを返却する
	 * 
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @param season 時期情報
	 */
	@Override
	public UserTestFunctionView findAllTestUserFunctionView(int year, int grade, String className, String season) {
		return userTestRepository.findAllUserTestFunctionView(year, grade, className, season);
	}

	/**
	 * ユーザー名を元に在籍した学年・クラス情報の最新の年度・クラス名・学年情報を抽出する
	 * 
	 * @param userName ユーザー名(氏名情報ではなく一意のユーザー情報)
	 */
	@Override
	public UserGradeClassView findUserGradeClassView(String userName) {
		return userGradeClassRepository.findUserGradeGlassView(userName);
	}

	/**
	 * 年度情報を元にその年度の登録されているテスト結果の時期情報を取得し返却する
	 * 
	 * @param year 年度
	 * @return
	 */
	@Override
	public List<String> findAllSeasonByYear(int year) {
		return userTestRepository.findAllSeasonByYear(year);
	}

	/**
	 * 年度情報を元にその年度の登録されているテスト結果の時期情報を取得し返却する
	 * 
	 * @param year      年度
	 * @param grade     学年
	 * @param className クラス名
	 * @return
	 */
	@Override
	public List<String> findAllSeasonByYear(int year, int grade, String className) {
		return userTestRepository.findAllSeasonByYear(year, grade, className);
	}

	/**
	 * AppUserテーブルから指定されたユーザー名と一致した要素数を返却
	 * 
	 * @param userName AppUserテーブルに指定するUserName
	 */
	@Override
	public long finduserNameCount(String userName) {
		return userRepository.count(UserHelper.equalUserName(userName));
	}

	/**
	 * ユーザーに新規ユーザー名とエンコードしたパスワードを設定して更新するメソッド
	 * 
	 * @param appUser     ユーザーオブジェクト
	 * @param newPassword パスワード
	 * @param newUserName ユーザー名(userName)
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public AppUser saveAppUser(AppUser appUser, String newPassword, String newUserName) {
		appUser.setUserName(newUserName);
		appUser.setPassword(passwordEncoder.encode(newPassword));
		return userRepository.save(appUser);
	}

	// ********************** private ***************************

	/**
	 * 学年クラス情報・ユーザーの中間テーブルの情報を、年度と学年を指定して取得する
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @return 年度情報と学年情報から検索された中間テーブルデータ
	 */
	private List<UserGradeClass> findAllByYearAndGrade(int year, int grade){
		return userGradeClassRepository.findAll(
				Specification.where(UserGradeClassHelper.fetchGradeClassAndUser())
						.and(UserGradeClassHelper.equalYear(year))
						.and(UserGradeClassHelper.equalGrade(grade))
		);
	}

	/**
	 * 学年クラス情報を、年度と学年を指定して取得する
	 * @param year 年度情報
	 * @param grade 学年情報
	 * @return 年度情報と学年情報から検索された学年クラス情報
	 */
	private List<GradeClass> findAllGradeClassByYearAndGrade(int year, int grade){
		return gradeClassRepository.findAll(
				Specification.where(GradeClassHelper.equalYear(year))
						.and(GradeClassHelper.equalGrade(grade))
		);
	}
}
