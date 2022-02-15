package com.example.demo.controller;

import static com.example.demo.common.SchoolCommonValue.AUTHORITY_STUDENT;
import static com.example.demo.common.SchoolCommonValue.AUTHORITY_STUDENT_AND_TEACHER;
import static com.example.demo.common.SchoolCommonValue.AUTHORITY_TEACHER;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.config.security.service.AppUserDetails;
import com.example.demo.controller.excel.SchoolExcelView;
import com.example.demo.controller.excel.SchoolStudentExcelView;
import com.example.demo.controller.excel.TestTotalScoreExcelView;
import com.example.demo.controller.form.CreateExcelTemplateForm;
import com.example.demo.controller.form.CreateStudentExcelTemplateForm;
import com.example.demo.controller.form.FormEnum;
import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.DBInjectType;
import com.example.demo.controller.form.FormEnum.Grade;
import com.example.demo.controller.form.FormEnum.RegistrationData;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.SortClass;
import com.example.demo.controller.form.FormEnum.SortPoint;
import com.example.demo.controller.form.FormEnum.Subject;
import com.example.demo.controller.form.GradeClassForm;
import com.example.demo.controller.form.NewTestRegisterForm;
import com.example.demo.controller.form.PasswordChangeForm;
import com.example.demo.controller.form.SchoolDataForm;
import com.example.demo.controller.form.StudentSearchForm;
import com.example.demo.controller.form.StudentTestForm;
import com.example.demo.controller.form.SumSurveyForm;
import com.example.demo.controller.form.SurveyRecordForm;
import com.example.demo.controller.form.SurveyStudentRecordForm;
import com.example.demo.controller.util.PageWrapper;
import com.example.demo.domain.grade.model.GradeClass;
import com.example.demo.domain.grade.model.UserGradeClass;
import com.example.demo.domain.grade.model.propagation.UserGradeClassView;
import com.example.demo.domain.school.SchoolService;
import com.example.demo.domain.test.model.Test;
import com.example.demo.domain.test.model.UserTest;
import com.example.demo.domain.test.model.propagation.UserTestFunctionView;
import com.example.demo.domain.test.model.propagation.UserTestView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// SQLIntegrityConstraintViolationException ユーザー情報が存在しない状態でTest情報などを登録した場合
// SQLIntegrityConstraintViolationException UserIdが他のデータと被った場合

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("school")
@SessionAttributes(types = { SurveyRecordForm.class, StudentTestForm.class })
public class SchoolController {

	@ModelAttribute("surveyRecordForm")
	public SurveyRecordForm surveyRecordForm() {
		return new SurveyRecordForm();
	}

	@ModelAttribute("studentTestForm")
	public StudentTestForm studentTestForm() {
		return new StudentTestForm();
	}

	/**
	 * 現在の年から2年前・2年後の5年分の年代をInteger形式のリストで返却する
	 */
	@ModelAttribute("year")
	public List<Integer> years() {
		return Stream.iterate(LocalDate.now().getYear() - 2, i -> i + 1).limit(5)
				.collect(Collectors.toList());
	}

	private final SchoolService schoolService;

	@ModelAttribute("username")
	public String username() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * ログインページ
	 */
	@GetMapping("login")
	public String getLogin() {
		return "login/login";
	}

	/**
	 * メインページ
	 */
	@GetMapping("main")
	public String getMain(Model model) {
		model.addAttribute("pageTitle", "メインページ");
		return "school/main";
	}

	/**
	 * データの登録ページ
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("registration")
	public String getRegistration(
			@ModelAttribute("schoolDataForm") SchoolDataForm form, Model model) {
		model.addAttribute("pageTitle", "データ登録ページ");
		model.addAttribute("dataType", RegistrationData.values());
		model.addAttribute("dbInjectType", DBInjectType.values());
		return "school/data-registration";
	}

	/**
	 * データの登録ページのファイルアップロード処理
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@PostMapping("registration/file")
	public String postRegistration(
			@Validated @ModelAttribute("schoolDataForm") SchoolDataForm form,
			BindingResult bindingResult,
			@ModelAttribute("createExcelTemplateForm") CreateExcelTemplateForm excelForm,
			UriComponentsBuilder uriBuilder,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "データ登録ページ");
			model.addAttribute("dataType", RegistrationData.values());
			model.addAttribute("dbInjectType", DBInjectType.values());
			return "school/data-registration";
		}
		String uri = MvcUriComponentsBuilder.relativeTo(uriBuilder).withMappingName("SC#getRegistration").encode().build();
		if (form.getRegistrationData() == RegistrationData.DATA_STUDENT) {
			// 学生データを選択されていた場合は
			if (form.getDbInjectType() == DBInjectType.NEW_INSERT) {
				// 新規挿入の場合
				List<UserGradeClass> result = schoolService.insertAllGradeClass(form);
				redirectAttributes.addFlashAttribute(
						"registrationMessage",result.size() + "件のユーザー情報を新規登録しました");
			} else {
				// 更新の場合
				List<UserGradeClass> result = schoolService.updateAllGradeClasses(form);
				redirectAttributes.addFlashAttribute(
						"registrationMessage", result.size() + "件のユーザー情報を更新しました"
				);
			}
		} else {
			// テストデータを選択されていた場合
			if (form.getDbInjectType() == DBInjectType.NEW_INSERT) {
				// 新規挿入の場合
				List<UserTest> result = schoolService.insertAllStudentTest(form);
				redirectAttributes.addFlashAttribute(
						"registrationMessage", result.size() + "件のテストデータを新規登録しました");
			} else {
				// 更新の場合
				List<UserTest> result = schoolService.updateAllStudentTest(form);
				redirectAttributes.addFlashAttribute(
						"registrationMessage", result.size() + "件のテストデータを更新しました");
			}
		}
		return "redirect:" + uri;
	}

	/**
	 * テストテンプレートのダウンロードページ
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("test-template/download")
	public String getTestTemplateDownload(@ModelAttribute("createExcelTemplateForm") CreateExcelTemplateForm excelForm,
			Model model) {
		model.addAttribute("pageTitle", "テスト用テンプレートダウンロード");
		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("clazz", FormEnum.Clazz.valuesExcudeAllArray());
		model.addAttribute("season", FormEnum.Season.values());
		model.addAttribute("subject", FormEnum.Subject.valuesExcludeAllArray());
		return "school/test-template-download";
	}

	/**
	 * testテンプレートダウンロードのページのExcelダウンロード処理
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@PostMapping("test-template/download")
	public ModelAndView postDownloadExcelFile(
			@Validated @ModelAttribute("createExcelTemplateForm") CreateExcelTemplateForm excelForm,
			BindingResult bindingResult) {
		System.out.println(excelForm);
		ModelAndView mav = new ModelAndView();
		if (bindingResult.hasErrors()) {
			mav.addObject("pageTitle", "テスト用テンプレートダウンロード");
			mav.addObject("grade", FormEnum.Grade.values());
			mav.addObject("clazz", FormEnum.Clazz.valuesExcudeAllArray());
			mav.addObject("season", FormEnum.Season.values());
			mav.addObject("subject", FormEnum.Subject.valuesExcludeAllArray());
			mav.setViewName("school/test-template-download");
			return mav;
		}
		List<UserGradeClass> resultList = schoolService.findSearchGradeClass(excelForm.getYear(), excelForm.getGrade(),
				excelForm.getClazz());
		mav.addObject("studentGradeClassList", resultList);
		mav.addObject("form", excelForm);
		mav.setView(new SchoolExcelView());
		return mav;
	}

	/**
	 * 成績調査ページ
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("survey-form")
	public String getRecordSurvey(
			@PageableDefault(size = 50, page = 0, direction = Direction.ASC, sort = { "className" }) Pageable pageable,
			UriComponentsBuilder uriBuilder, @AuthenticationPrincipal UserDetails user,
			@ModelAttribute("surveyRecordForm") SurveyRecordForm form, Model model) {
		System.out.println(form);
		PageWrapper<UserTest> page;
		Sort sort = form.getSortPointOption().getSort().and(form.getSortClassOption().getSort());
		String uri = MvcUriComponentsBuilder.relativeTo(uriBuilder).withMappingName("SC#getRecordSurvey").encode()
				.build();

		Pageable pag = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		if (form.isFirstAccess()) {
			// 初めてのアクセスの場合はform情報を初期化
			initSurveyRecordForm(form, user.getUsername());
		}

		page = new PageWrapper<>(schoolService.findAllRequestTestInformation(form, pag), 5, uri);

		pageable.getSort().stream().forEach(System.out::println);
		model.addAttribute("pageTitle", "成績調査");
		model.addAttribute("requestList", page.getContent());
		model.addAttribute("page", page);
		model.addAttribute("items", page.getItems());

		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("clazz", FormEnum.Clazz.values());
		model.addAttribute("season", FormEnum.Season.values());
		model.addAttribute("subject", FormEnum.Subject.values());
		model.addAttribute("sortPoint", SortPoint.values());
		model.addAttribute("sortClass", SortClass.values());
		return "school/survey-form";
	}

	/**
	 * アクセスが初めての場合にSurveyRecordForm情報を初期化するメソッド
	 * 
	 * @param form surveyRecordForm
	 * @param userName ユーザー名(氏名ではなく一意のユーザー名)
	 */
	private void initSurveyRecordForm(SurveyRecordForm form, String userName) {
		int nowYear = LocalDate.now().getYear();

		form.setFirstAccess(false);
		form.setSubject(Subject.ALL);
		form.setYear(nowYear);

		UserGradeClassView view = schoolService.findUserGradeClassView(userName);
		if (view.getYear() == null || view.getYear() < nowYear) {
			// 年度が無い及び現在の年度より前の場合
			form.setClazz(Clazz.ALL);
			form.setGrade(Grade.ONE);
			List<String> seasons = schoolService.findAllSeasonByYear(nowYear);
			if (CollectionUtils.isEmpty(seasons)) {
				// seasons情報が無い場合
				form.addSeason(Season.MIDDLE_FIRST_SEMESTER);
			} else {
				form.addSeason(Season.getSeasonBySeasonName(seasons.get(0)));
			}
		} else {
			// 年度が今年かつ現在の年度
			form.setClazz(Clazz.getClazzEnum(view.getClazzName()));
			form.setGrade(Grade.getMatchGrade(view.getGrade()));
			List<String> seasons = schoolService.findAllSeasonByYear(nowYear, view.getGrade(), view.getClazzName());
			if (CollectionUtils.isEmpty(seasons)) {
				// seasons情報が無い
				form.addSeason(Season.MIDDLE_FIRST_SEMESTER);
			} else {
				form.addSeason(Season.getSeasonBySeasonName(seasons.get(0)));
			}
		}
	}

	/**
	 * 既存のテストデータを単品で更新するページ
	 * 
	 * @param studentTestId 学生のテストID(一意の値)
	 * @param form studentTestForm
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("student/survey-form/update-test/{studentTestId}")
	public String getStudentTestUpdate(@PathVariable("studentTestId") Long studentTestId,
			@ModelAttribute("studentTestForm") StudentTestForm form, Model model) {
		UserTest studentTest = schoolService.findByUserTestId(studentTestId);
		form.setStudentTestId(studentTestId);
		form.setPoint(studentTest.getPoint());

		model.addAttribute("studentTest", studentTest);
		model.addAttribute("pageTitle", "テスト情報更新");

		System.out.println("getStudentTestUpdate:" + model);
		return "school/student-test-update";
	}

	/**
	 * 既存のテストデータを単品で更新するリクエスト
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@PostMapping("student/survey-form/update-test")
	public String postStudentTestUpdate(
			@Validated @ModelAttribute("studentTestForm") StudentTestForm form,
			BindingResult bindingResult, Model model, UriComponentsBuilder uriBuilder,
			RedirectAttributes redirectAttributes) {
		System.out.println(form);
		if (bindingResult.hasErrors()) {
			UserTest studentTest = schoolService.findByUserTestId(form.getStudentTestId());
			form.setPoint(studentTest.getPoint());

			model.addAttribute("studentTest", studentTest);
			model.addAttribute("pageTitle", "テスト情報更新");
			return "school/student-test-update";
		}
		String uri = MvcUriComponentsBuilder.relativeTo(uriBuilder).withMappingName("SC#getStudentTestUpdate")
				.arg(0, form.getStudentTestId()).build();

		// 更新処理を実装する
		schoolService.singleUpdateUserTest(form.getStudentTestId(), form.getPoint());

		System.out.println("postStudentTestUpdate:" + model); // test
		redirectAttributes.addFlashAttribute("complete", "更新が完了しました");

		return "redirect:" + uri;
	}

	/**
	 * 合計点調査ページ
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("sum/survey-form")
	public String getSumSurveyForm(
			@ModelAttribute("sumSurveyForm") SumSurveyForm form, Model model) {
		if (form.yearGradeClazzIsNull()) {
			form.setYear(LocalDate.now().getYear());
			form.setClazz(Clazz.ALL);
			form.setSeason(Season.MIDDLE_FIRST_SEMESTER);
			form.setGrade(Grade.ONE);
		}

		Pageable pageable = PageRequest.of(0, form.getDisplay().getDisplayCount(), form.getSort().getSort());
		Page<UserTestView> result = null;

		UserTestFunctionView funcResult = null;
		if (form.getClazz() == Clazz.ALL) {
			result = schoolService.findAllTestUserView(form.getYear(),form.getGrade().getGrade(),
					form.getSeason().getSeasonName(), pageable);
			funcResult = schoolService.findAllTestUserFunctionView(form.getYear(), form.getGrade().getGrade(),
					form.getSeason().getSeasonName());
		} else {
			result = schoolService.findAllTestUserView(form.getYear(), form.getGrade().getGrade(),
					form.getClazz().name(), form.getSeason().getSeasonName(), pageable);
			funcResult = schoolService.findAllTestUserFunctionView(form.getYear(), form.getGrade().getGrade(),
					form.getClazz().name(), form.getSeason().getSeasonName());
		}

		result.getContent()
				.forEach(el -> log.info("firstName: {}, lastName: {}, className: {}, totalPoint: {}", el.getFirstName(),
						el.getLastName(), el.getClazzName(), el.getSumPoint()));

		model.addAttribute("sumTestList", result.getContent());
		model.addAttribute("testResult", funcResult);

		model.addAttribute("tableTitle2", form.getClazz().getClazzName() + "の合計点");
		model.addAttribute("pageTitle", "合計点調査");
		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("clazz", FormEnum.Clazz.values());
		model.addAttribute("season", FormEnum.Season.values());
		model.addAttribute("sort", FormEnum.SortSumPoint.values());
		model.addAttribute("display", FormEnum.NumDisplay.values());
		return "school/sum-survey-form";
	}

	/**
	 * 合計点調査結果のExcel出力
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping(path = "sum/survey-form", params = "download")
	public ModelAndView getSumSurveyFormDownLoad(
			@Validated @ModelAttribute("sumSurveyForm") SumSurveyForm form, BindingResult bindingResult) {
		ModelAndView mav = new ModelAndView();
		if (bindingResult.hasErrors()) {
			mav.addObject("pageTitle", "Excel出力");
			mav.addObject("grade", FormEnum.Grade.values());
			mav.addObject("clazz", FormEnum.Clazz.values());
			mav.addObject("season", FormEnum.Season.values());
			mav.addObject("sort", FormEnum.SortSumPoint.values());
			mav.addObject("display", FormEnum.NumDisplay.values());
			mav.setViewName("school/sum-survey-form");
			return mav;
		}

		Pageable pageable = PageRequest.of(0, form.getDisplay().getDisplayCount(), form.getSort().getSort());
		Page<UserTestView> result = null;

		if (form.getClazz() == Clazz.ALL) {
			result = schoolService.findAllTestUserView(form.getYear(), form.getGrade().getGrade(),
					form.getSeason().getSeasonName(), pageable);
		} else {
			result = schoolService.findAllTestUserView(form.getYear(), form.getGrade().getGrade(),
					form.getClazz().name(), form.getSeason().getSeasonName(), pageable);
		}
		mav.addObject("studentSumTestList", result.getContent());
		mav.addObject("sumSurveyForm", form);
		mav.setView(new TestTotalScoreExcelView());
		return mav;
	}

	/**
	 * 個人の成績調査ページ
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("student/survey-form/{studentId}")
	public String getStudentSurvey(
			@PathVariable("studentId") Long studentId,
			Model model, @ModelAttribute("surveyStudentRecordForm") SurveyStudentRecordForm form) {
		if (form.allunselected()) {

			List<UserTest> tests = schoolService.findAllSingleStudentTests(studentId, null, null, null);
			surveyStudentRecordFormInit(form, tests);
		}
		model.addAttribute("pageTitle", "個人成績調査");
		model.addAttribute("studentId", studentId);
		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("season", FormEnum.Season.values());
		model.addAttribute("subject", FormEnum.Subject.values());
		model.addAttribute("requestList", schoolService.findAllSingleStudentTests(studentId, form.getGrade(),
				form.getSubject(), form.getSeason()));
		return "school/student-survey-form";
	}

	/**
	 * 検索フォームの初期化
	 */
	private void surveyStudentRecordFormInit(SurveyStudentRecordForm form, List<UserTest> tests) {
		// 学年をセット
		int latestGrade = tests.stream().map(test -> test.getTest().getGrade()).distinct().max(Integer::compareTo).get();
		form.setGrade(Grade.gradeEnum(latestGrade));
		// 時期情報をセット
		tests.stream().map(test -> Season.seasonEnum(test.getTest().getSeasonName())).distinct()
				.forEach(form::addSeason);
		// 全教科をセット
		form.setSubject(Subject.ALL);
	}

	/**
	 * 個人の成績調査ページ
	 */
	@PreAuthorize(AUTHORITY_STUDENT)
	@GetMapping("student/survey-form")
	public String getStudentSurvey(@AuthenticationPrincipal UserDetails user,
			@ModelAttribute("surveyStudentRecordForm") SurveyStudentRecordForm form, Model model) {
		if (form.allunselected()) {
			// 全て未検索の状態の場合初期化処理
			UserGradeClassView view = schoolService.findUserGradeClassView(user.getUsername());
			form.setGrade(Grade.getMatchGrade(view.getGrade()));
			form.setSubject(Subject.ALL);
			List<String> seasons = schoolService.findAllSeasonByYear(view.getYear(), view.getGrade(),
					view.getClazzName());
			if (CollectionUtils.isEmpty(seasons)) {
				form.addSeason(Season.MIDDLE_FIRST_SEMESTER);
			} else {
				form.addSeason(Season.getSeasonBySeasonName(seasons.get(0)));
			}
		}
		Long userId = ((AppUserDetails) user).getAppUser().getUserId();
		model.addAttribute("pageTitle", "個人成績調査");
		model.addAttribute("role", user.getAuthorities().stream().findFirst().get().getAuthority());
		model.addAttribute("studentId", userId);

		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("season", FormEnum.Season.values());
		model.addAttribute("subject", FormEnum.Subject.values());
		model.addAttribute("requestList",
				schoolService.findAllSingleStudentTests(userId, form.getGrade(), form.getSubject(), form.getSeason()));
		return "school/student-survey-form";
	}

	/**
	 * 試験データ挿入ページ
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("test/registration")
	public String getTestRegistration(@ModelAttribute("newTestRegisterForm") NewTestRegisterForm form, Model model) {
		model.addAttribute("pageTitle", "試験データ挿入");
		return "school/test-registration";
	}

	/**
	 * 試験データ挿入処理
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@PostMapping("test/registration")
	public String postTestRegistration(@Validated @ModelAttribute("newTestRegisterForm") NewTestRegisterForm form,
			BindingResult bindingResult, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "試験データ挿入");
			return "school/test-registration";
		}
		List<Test> resultList = schoolService.createTestInformationOfYear(form.getYear());
		String redirectPath = MvcUriComponentsBuilder.relativeTo(uriBuilder).withMappingName("SC#getTestRegistration")
				.build();
		redirectAttributes.addFlashAttribute("registrationNum", resultList.size() + "件のテスト情報を登録しました");
		return "redirect:" + redirectPath;
	}

	/**
	 * 学年・クラス情報挿入ページ ※追加でテストテーブルも合わせて作成するように見直す※
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("grade-class/registration")
	public String getGradeClassRegistration(Model model, @ModelAttribute("gradeClassForm") GradeClassForm form) {
		model.addAttribute("pageTitle", "学年・クラス情報作成");
		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("clazz", FormEnum.Clazz.ALL.valuesExcludeAll());
		return "school/grade-class-registration";
	}

	/**
	 * 学年・クラス情報挿入処理
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@PostMapping("grade-class/registration")
	public String postGradeClassRegistration(@Validated @ModelAttribute("gradeClassForm") GradeClassForm form,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, UriComponentsBuilder uriBuilder,
			Model model) {
		System.out.println(form);
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "学年・クラス情報作成");
			model.addAttribute("grade", FormEnum.Grade.values());
			model.addAttribute("clazz", FormEnum.Clazz.ALL.valuesExcludeAll());
			return "school/grade-class-registration";
		}
		List<GradeClass> requestResult = schoolService.createGradeClassInformation(form);
		redirectAttributes.addFlashAttribute("registrationNum", requestResult.size() + "件の学年クラス情報を挿入しました");
		String redirectPath = MvcUriComponentsBuilder.relativeTo(uriBuilder)
				.withMappingName("SC#getGradeClassRegistration").build();
		return "redirect:" + redirectPath;
	}

	/**
	 * パスワード変更フォーム
	 */
	@PreAuthorize(AUTHORITY_STUDENT_AND_TEACHER)
	@GetMapping("/password-change")
	public String getStudentPasswordChange(@AuthenticationPrincipal AppUserDetails user,
			@ModelAttribute("passwordChangeForm") PasswordChangeForm form, Model model) {
		model.addAttribute("pageTitle", "パスワード変更");
		form.setUserName(user.getUsername());
		return "school/password-change";
	}

	/**
	 * パスワード・ユーザー名変更フォーム
	 */
	@PreAuthorize(AUTHORITY_STUDENT_AND_TEACHER)
	@PostMapping("/password-change")
	public String postStudentPasswordChange(@AuthenticationPrincipal AppUserDetails user,
			@Validated @ModelAttribute("passwordChangeForm") PasswordChangeForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model, UriComponentsBuilder uriBuilder) {
		long count = schoolService.finduserNameCount(form.getUserName());
		log.info("count : {} ", count);
		// 1が返却された場合は登録済みのユーザーがある為、この場合も戻る必要あり
		// ※本来であればメールアドレスにするべきだが、ここではサンプルの為このような処理になっている※
		if (count == 1) {
			FieldError error = new FieldError("userName", "userName", "指定されたユーザーIDは利用できません");
			bindingResult.addError(error);
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "パスワード変更");
			return "school/password-change";
		}
		String uri = MvcUriComponentsBuilder.relativeTo(uriBuilder).withMappingName("SC#getStudentPasswordChange")
				.build();
		schoolService.saveAppUser(user.getAppUser(), form.getPassword(), form.getUserName());
		redirectAttributes.addFlashAttribute("complete", "パスワード・ユーザー名の変更が完了しました");
		// ※@AuthenticationPrincipal AppUserDetails userのユーザー情報が更新されないっぽいｎ※
		return "redirect:" + uri;
	}

	/**
	 * 学生検索ページ
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("search-student")
	public String getSearchStudent(@ModelAttribute("studentSearchForm") StudentSearchForm form, Model model) {
		form.setYear(LocalDate.now().getYear());
		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("clazz", FormEnum.Clazz.ALL.valuesExcludeAll());
		return "school/student-search-form";
	}

	/**
	 * 学生検索ページ
	 *
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping(path = "search-student/execution")
	public String getSearchStudentExecution(
			@PageableDefault(size = 50, page = 0, direction = Direction.ASC, sort = {
					"gradeClass.className" }) Pageable pageable,
			@ModelAttribute("studentSearchForm") StudentSearchForm form, Model model, UriComponentsBuilder builder) {
		System.out.println(form);
		Page<UserGradeClass> pageList = schoolService.findSearchGradeClass(form.getYear(), form.getGrade(),
				form.getClazz(), form.getFirstName(), form.getLastName(), pageable);
		String uri = MvcUriComponentsBuilder.relativeTo(builder).withMappingName("SC#getSearchStudentExecution").build();
		PageWrapper<UserGradeClass> page = new PageWrapper<>(pageList, 5, uri);

		model.addAttribute("requestList", page.getContent());
		model.addAttribute("page", page);
		model.addAttribute("items", page.getItems());

		model.addAttribute("grade", FormEnum.Grade.values());
		model.addAttribute("clazz", FormEnum.Clazz.ALL.valuesExcludeAll());
		return "school/student-search-form";
	}

	/**
	 * 翌年の学生情報のテンプレートダウンロードページ
	 *
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@GetMapping("student-template/download")
	public String getStudentTemplateDownload(
			@ModelAttribute("createStudentExcelTemplateForm") CreateStudentExcelTemplateForm form, Model model) {
		model.addAttribute("grade", FormEnum.Grade.values());
		return "school/student-template";
	}

	/**
	 * 翌年の学生情報のテンプレートダウンロードリクエスト ※現在2・3学年の方は考慮されているが1学年の際はヘッダーのみ出力される状態なので
	 * 1学年の際の処理も考える必要あり、例えば人数などの指定やUserNameの生成などが考えられる※
	 *
	 */
	@PreAuthorize(AUTHORITY_TEACHER)
	@PostMapping("student-template/download")
	public ModelAndView postStudentTemplateDownload(
			@Validated @ModelAttribute("createStudentExcelTemplateForm") CreateStudentExcelTemplateForm form,
			BindingResult bindingResult) {
		ModelAndView mav = new ModelAndView();
		if (bindingResult.hasErrors()) {
			mav.addObject("grade", FormEnum.Grade.values());
			mav.setViewName("school/student-template");
			return mav;
		}
		List<UserGradeClass> studentGradeClassList = schoolService.findSearchGradeClass(form.getYear() - 1,
				Grade.getMatchGrade(form.getGrade().getGrade() - 1), null);

		mav.addObject("studentGradeClassList", studentGradeClassList);
		mav.addObject("grade", FormEnum.Grade.values());
		mav.addObject("form", form);
		mav.setView(new SchoolStudentExcelView());
		return mav;
	}
}
