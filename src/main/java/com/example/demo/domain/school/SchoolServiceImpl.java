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
import com.example.demo.domain.excel.ExcelInformationReader.NecessaryInformation;
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
		// マップを一つ抽出
		Map<String, String> firstElementMap = informationListMap.get(0);
		// 年度情報
		int year = NumberUtils.toInt(firstElementMap.get(YEAR));
		// 実在する学年クラス情報(year年度の)
		List<GradeClass> gradeClassList = gradeClassRepository.findByYear(year);
		if (gradeClassList.isEmpty()) {
			// 年度(year)に対応したクラス情報が存在しない場合に送出する
			throw new GradeClassNotRegisterException(
					"現在" + year + "年度の学年・クラス情報が登録されていません。" + year + "年度の学年・クラス情報を先に登録必要があります");
		}
		// 既に登録済みで無いかのチェック
		allReadyRegisterStudentCheck(informationListMap, year);

		// 学生用ロール情報取得
		Role role = roleRepository.findByRoleName(ROLE_STUDENT);
		// 保存用学生リスト(ID無し)
		List<AppUser> students = new ArrayList<>();
		// 保存用学生リストに対応した学年クラス情報
		List<GradeClass> gradeClasses = new ArrayList<>();
		// 保存用リストに要素を格納
		for (Map<String, String> map : informationListMap) {
			String password = passwordEncoder.encode(PASSWORD); // エンコードしたパスワード
			AppUser user = AppUser.of(map.get(USER_NAME), password, map.get(FIRST_NAME), map.get(LAST_NAME), role);
			students.add(user);
			for (GradeClass gc : gradeClassList) {
				if (Objects.equal(gc.getGrade(), NumberUtils.toByte(map.get(GRADE)))
						&& Objects.equal(gc.getClassName(), map.get(CLASS_NAME))) {
					// 学年とクラス名が等しければ
					gradeClasses.add(gc);
					break;
				}
			}
		}
		// DBに保存された学生情報リスト(ID持ち)
		List<AppUser> createdStudents = userRepository.saveAll(students);
		// 保存用ユーザー(学生と学年クラス)の中間テーブルリスト
		List<UserGradeClass> userGradeClasses = new ArrayList<>();

		for (int i = 0; i < createdStudents.size(); i++) {
			UserGradeClass ugc = new UserGradeClass();
			ugc.setAppUser(createdStudents.get(i));
			ugc.setGradeClass(gradeClasses.get(i));
			userGradeClasses.add(ugc);
		}
		// 中間テーブルの保存結果
		List<UserGradeClass> createdUserGradeClasses = userGradeClassRepository.saveAll(userGradeClasses);
		return createdUserGradeClasses;
	}

	/**
	 * 学生に対応する学年クラス中間テーブルの新規挿入用、主に昇級した際に使われる想定
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<UserGradeClass> updateAllGradeClass(SchoolDataForm form) {
		List<Map<String, String>> informationListMap = createResultList(form);
//		log.info(informationListMap.toString());
		// マップを一つ抽出
		Map<String, String> firstElementMap = informationListMap.get(0);
		// 年度情報
		int year = NumberUtils.toInt(firstElementMap.get(YEAR));
		// 実在する学年クラス情報(year年度の)
		List<GradeClass> gradeClassList = gradeClassRepository.findByYear(year);
		if (gradeClassList.isEmpty()) {
			// 年度(year)に対応したクラス情報が存在しない場合に送出する
			throw new GradeClassNotRegisterException(
					"現在" + year + "年度の学年・クラス情報が登録されていません。" + year + "年度の学年・クラス情報を先に登録必要があります");
		}

		// 昨年度の情報
		List<UserGradeClass> ugcList = userGradeClassRepository.findAll(
				Specification.where(UserGradeClassHelper.fetchUser()).and(UserGradeClassHelper.equalYear(year - 1)));
		List<AppUser> userList = ugcList.stream().map(ugc -> ugc.getAppUser()).collect(Collectors.toList());

		// update用中間テーブルクラス
		List<UserGradeClass> updateList = new ArrayList<>();

		for (Map<String, String> map : informationListMap) {
			UserGradeClass ugc = new UserGradeClass();
			for (AppUser user : userList) {
				if (Objects.equal(user.getUserId(), map.get(USER_ID))) {
					// map内のユーザーIDとDBに格納されているユーザーIDが等しい
					ugc.setAppUser(user);
					break;
				}
			}
			for (GradeClass gc : gradeClassList) {
				if (Objects.equal(gc.getGrade(), NumberUtils.toByte(map.get(GRADE)))
						&& Objects.equal(gc.getClassName(), map.get(CLASS_NAME))) {
					// 学年の情報とクラス名の情報が等しい
					ugc.setGradeClass(gc);
					break;
				}
			}
			updateList.add(ugc);
		}
//		log.info(updateList.toString());

		List<UserGradeClass> createdUserGradeClasses = userGradeClassRepository.saveAll(updateList);
		return createdUserGradeClasses;
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
	 * @param firstElementMap
	 * @param year
	 */
	private void alreadyRegisterTestCheck(Map<String, String> firstElementMap, int year) {
		String className = firstElementMap.get(CLASS_NAME);
		byte grade = NumberUtils.toByte(firstElementMap.get(GRADE));
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
	 * @param informationList
	 * @return
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
				.findAll(Specification.where(TestHelper.equalYear(year)).and(TestHelper.equalSeaxon(seasonName)));
		if (dbTests.isEmpty()) {
			throw new TestInformationNotRegisterException(year + "年度の" + seasonName + "は現在DBに登録されていません。確認後登録してください。");
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
							&& Objects.equal(t.getGrade(), NumberUtils.toByte(map.get(GRADE))))
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
		List<Byte> grades = informationList.stream().map(m -> NumberUtils.toByte(m.get(GRADE))).distinct()
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
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	@Override
	public List<GradeClass> createGradeClassInfomation(GradeClassForm form) {
		// リクエストに選択された情報から現在DBに登録されている学年・クラス情報を抽出(抽出元情報：学年・クラスリスト・年度)
		List<GradeClass> dbClasses = gradeClassRepository
				.findAll(Specification.where(GradeClassHelper.equalYear(form.getYear()))
						.and(GradeClassHelper.equalGrade((byte) form.getGrade().getGrade()))
						.and(GradeClassHelper.equalClassNames(form.getClassList())));
		// リクエストに選択された情報から作成した学年・クラス情報を抽出
		List<GradeClass> gradeClasses = new ArrayList<>();

		if (dbClasses.isEmpty()) {
			// かぶっている要素が無い
			for (int i = 0; i < form.getClassList().size(); i++) {
				GradeClass gc = new GradeClass();
				gc.setClassName(form.getClassList().get(i).name());
				gc.setGrade((byte) form.getGrade().getGrade());
				gc.setYear(form.getYear());
				gradeClasses.add(gc);
			}
		} else {
			// かぶっている要素がある
			List<String> classNames = dbClasses.stream().map(c -> c.getClassName()).collect(Collectors.toList());
			for (int i = 0; i < form.getClassList().size(); i++) {
				if (classNames.contains(form.getClassList().get(i).name())) {
					continue;
				}
				GradeClass gc = new GradeClass();
				gc.setClassName(form.getClassList().get(i).name());
				gc.setGrade((byte) form.getGrade().getGrade());
				gc.setYear(form.getYear());
				gradeClasses.add(gc);
			}
		}

		log.info(dbClasses.toString());
		log.info(gradeClasses.toString());
		return gradeClassRepository.saveAll(gradeClasses);
	}

	/**
	 * 指定された年度のテスト情報を新たに挿入するメソッド 既にDBに登録されている学年・クラス情報(GradeClassテーブル)を元に作成する
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public List<Test> createTestInfomationOfYear(int year) {
		// 指定された年度の学年クラスリスト
		List<GradeClass> dbGradeClasses = gradeClassRepository.findAll(GradeClassHelper.equalYear(year));
		// 登録されていた学年を絞り込み
		List<Byte> grades = dbGradeClasses.stream().map(gc -> gc.getGrade()).distinct().collect(Collectors.toList());
		if (dbGradeClasses.isEmpty()) {
			// DBに指定された年度の学年クラスが存在しない場合
			return new ArrayList<>();
		}
		// dbに登録されているyear年度のテスト
		List<Test> dbTests = testRepository.findAll(TestHelper.equalYear(year));
		// dbに新規登録するTest
		List<Test> createTests = new ArrayList<>();

		for (Byte grade : grades) {
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
	 * @param form
	 * @return
	 */
	private List<Map<String, String>> createResultList(SchoolDataForm form) {
		NecessaryInformation info = excelInformationReader.getNecessaryInformation(form.getRegistationData(),
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
	 */
	@Override
	public Page<UserTest> findAllRequestTestInfomation(SurveyRecordForm form, Pageable pageable) {
		return userTestRepository.findAll(Specification.where(UserTestHelper.fetchUserAndTest()
				.and(UserTestHelper.equalYear(form.getYear())).and(UserTestHelper.equalGrade(form.getGrade()))
				.and(UserTestHelper.equalClass(form.getClazz())).and(UserTestHelper.equalSeasons(form.getSeason()))
				.and(UserTestHelper.equalSubject(form.getSubject()))), pageable);
	}

	/**
	 * ユーザーID・学年・クラス・テスト時期・教科情報を元に1人のテスト情報を検索し返却するメソッド
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
	 */
	@Override
	public UserTest findByUserTestId(Long studentId) {
		return userTestRepository.findOne(
				Specification.where(UserTestHelper.fetchUserAndTest()).and(UserTestHelper.equalUserTestId(studentId)))
				.orElse(null);
	}

	/**
	 * 年度・学年・クラス・氏名情報を元にユーザー(学生)情報を検索する
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
	 * @param year
	 * @param grade
	 * @param season
	 * @param pageable
	 */
	@Override
	public Page<UserTestView> findAllTestUserView(int year, byte grade, String season, Pageable pageable) {
		return userTestRepository.findAllUserTestView(year, grade, season, pageable);
	}

	/**
	 * 年度・学年・クラス・時期・ページ数(Order情報込み)から個人のテスト時期の合計点を算出し返却するメソッド
	 * 
	 * @param year
	 * @param grade
	 * @param className
	 * @param season
	 * @param pageable
	 */
	@Override
	public Page<UserTestView> findAllTestUserView(int year, byte grade, String className, String season,
			Pageable pageable) {
		return userTestRepository.findAllUserTestView(year, grade, className, season, pageable);
	}

	/**
	 * 年度・学年・テスト時期の情報から生徒ごとの合計点を算出し、合計点の最大値・平均値・最小値を計算したデータを返却する
	 * 
	 * @param year
	 * @param grade
	 * @param season
	 */
	@Override
	public UserTestFunctionView findAllTestUserFunctionView(int year, byte grade, String season) {
		return userTestRepository.findAllUserTestFunctionView(year, grade, season);
	}

	/**
	 * 年度・学年・クラス・テスト時期の情報から生徒ごとの合計点を算出し、合計点の最大値・平均値・最小値を計算したデータを返却する
	 * 
	 * @param year
	 * @param grade
	 * @param season
	 */
	@Override
	public UserTestFunctionView findAllTestUserFunctionView(int year, byte grade, String className, String season) {
		return userTestRepository.findAllUserTestFunctionView(year, grade, className, season);
	}

	/**
	 * ユーザー名を元に在籍した学年・クラス情報の最新の年度・クラス名・学年情報を抽出する
	 * 
	 * @param userName
	 * @return
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
	public List<String> findAllSeasonByYear(int year, byte grade, String className) {
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

}
