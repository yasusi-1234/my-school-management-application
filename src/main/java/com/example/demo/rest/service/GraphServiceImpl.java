package com.example.demo.rest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.controller.form.FormEnum.Season;
import com.example.demo.controller.form.FormEnum.Subject;
import com.example.demo.domain.grade.model.propagation.UserGradeClassView;
import com.example.demo.domain.grade.repository.UserGradeClassRepository;
import com.example.demo.domain.test.model.propagation.AllClassAndSubjectView;
import com.example.demo.domain.test.model.propagation.AllClassOneSubjectView;
import com.example.demo.domain.test.model.propagation.AnySeasonAndAllSubjectView;
import com.example.demo.domain.test.model.propagation.AnySeasonTestAverageView;
import com.example.demo.domain.test.model.propagation.OneSeasonTestAverageView;
import com.example.demo.domain.test.model.propagation.OneStudentView;
import com.example.demo.domain.test.repository.UserTestRepository;
import com.example.demo.rest.form.DeviationGraphConfig;
import com.example.demo.rest.form.GraphConfig;
import com.example.demo.rest.form.StudentGraphConfig;
import com.example.demo.rest.model.DeviationGraphData;
import com.example.demo.rest.model.GraphData;
import com.example.demo.rest.model.StandardDeviation;
import com.example.demo.rest.model.StudentDeviationGraphConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GraphServiceImpl implements GraphService {

	private final UserTestRepository userTestRepository;

	private final UserGradeClassRepository userGradeClassRepository;

	/**
	 * 生徒用のグラフデータを返却するメソッド
	 */
	@Override
	public GraphData createIndividualGraphData(Long studentId, StudentGraphConfig graphConfig) {
		// 値が空・Nullの場合は全て選択したものとして考える
		if (graphConfig.getSeasonOption().isEmpty()) {
			Arrays.stream(Season.values()).forEach(season -> graphConfig.addSeasonOption(season));
		}
		if (Objects.isNull(graphConfig.getSubjectOption())) {
			graphConfig.setSubjectOption(Subject.ALL);
		}
		boolean multipleSeason = graphConfig.getSeasonOption().size() > 1; // テスト時期が2以上選択されている
		boolean allSubject = graphConfig.getSubjectOption() == Subject.ALL; // 全教科が選択されている

		// テスト時期が1つかつ全ての教科が選択されている
		if (!multipleSeason && allSubject) {
			return createAllSubjectAndOneSeason(studentId, graphConfig);
		}
		// 全ての教科が選択されているかつテスト時期が2以上選択されている
		if (allSubject && multipleSeason) {
			return createAllSubjectAndMultipleSeason(studentId, graphConfig);
		}
		// 教科が単数
		if (!allSubject) {
			return createOneSubjectAndOneOrMultipleSeason(studentId, graphConfig);
		}

		return null;
	}

	/**
	 * 全ての教科と複数の時期が選択された場合のグラフデータ作成メソッド 全ての教科が選択されているかつテスト時期が2以上選択されている
	 * 
	 * @param studentId
	 * @param graphConfig
	 * @return
	 */
	private GraphData createAllSubjectAndMultipleSeason(Long studentId, StudentGraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		int grade = graphConfig.getGradeOption().getGrade();
		List<String> seasons = graphConfig.getSeasonOption().stream().map(s -> s.getSeasonName())
				.collect(Collectors.toList());
		// 生徒のテスト情報
		List<OneStudentView> studentTestList = userTestRepository.findAnySeasonAllSubjectTestStudent(grade, studentId,
				seasons);

		// 生徒の所属のクラス
		Clazz studentClazz = null;
		// 年度
		int year = 0;

		if (CollectionUtils.isEmpty(studentTestList)) {
			UserGradeClassView view = userGradeClassRepository.findUserGradeClassView(studentId, grade);
			if (view == null) {
				return null;
			}
			studentClazz = Clazz.getClazzEnum(view.getClazzName());
			year = view.getYear();
		} else {
			studentClazz = Clazz.getClazzEnum(studentTestList.get(0).getClazzName());
			year = studentTestList.get(0).getYear();
		}

		// 生徒の学年のテスト情報
		List<AnySeasonAndAllSubjectView> gradeTestList = userTestRepository.findAnySeasonAndAllSubjects(year, grade,
				seasons);
		log.info("gradeTestList: {}", gradeTestList);
		// 生徒のクラスのテスト情報
		List<AnySeasonAndAllSubjectView> classTestList = userTestRepository.findAnySeasonAndAllSubjects(year, grade,
				studentClazz.name(), seasons);
		log.info("gradeTestList: {}", classTestList);
		// タイトル設定
		// **********************************
		String title = year + "年度の" + graphConfig.getSubjectOption().getSubjectName() + "の試験結果";
		graphData.setTitle(title);
		// ラベル設定
		graphConfig.getSeasonOption().stream().forEach(season -> graphData.addLabelName(season.getSeasonName()));
		// 各データ名設定
		String fullName = studentTestList.stream().map(s -> s.getLastName() + " " + s.getFirstName()).findFirst()
				.orElse("null");
		graphData.addUserName(fullName + "さんの合計点");
		graphData.addUserName(studentClazz.getClazzName() + "の合計点の平均値");
		graphData.addUserName(grade + "学年の合計点の平均値");
		// 各データの設定
		List<Integer> studentPoints = studentTestList.stream().map(s -> s.getUserPoint().intValue())
				.collect(Collectors.toList());
		List<Integer> classPoints = classTestList.stream().map(c -> c.getAvgPoint().intValue())
				.collect(Collectors.toList());
		List<Integer> gradePoints = gradeTestList.stream().map(c -> c.getAvgPoint().intValue())
				.collect(Collectors.toList());
		graphData.addPoints(studentPoints);
		graphData.addPoints(classPoints);
		graphData.addPoints(gradePoints);
		return graphData;
	}

	/**
	 * 全ての教科と一つの時期が選ばれた場合のグラフデータ作成メソッド // テスト時期が1つかつ全ての教科が選択されている
	 * 
	 * @param studentId
	 * @param graphConfig
	 * @return
	 */
	private GraphData createAllSubjectAndOneSeason(Long studentId, StudentGraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		String season = graphConfig.getSeasonOption().get(0).getSeasonName(); // 指定のテスト時期
		int grade = graphConfig.getGradeOption().getGrade();
		// 生徒のテスト情報
//		List<StudentTest> studentTestList = studentTestService.findByStudentIdAndGraphConfig(graphConfig, studentId);
		List<OneStudentView> studentTestList = userTestRepository.findOneSeasonTestStudent(grade, studentId, season);

		// 生徒の所属のクラス
		Clazz studentClazz = null;
		// 年度
		int year = 0;

		if (CollectionUtils.isEmpty(studentTestList)) {
			UserGradeClassView view = userGradeClassRepository.findUserGradeClassView(studentId, grade);
			if (view == null) {
				return null;
			}
			studentClazz = Clazz.getClazzEnum(view.getClazzName());
			year = view.getYear();
		} else {
			studentClazz = Clazz.getClazzEnum(studentTestList.get(0).getClazzName());
			year = studentTestList.get(0).getYear();
		}

		// 生徒の学年のテスト情報
		List<OneSeasonTestAverageView> gradeTestList = userTestRepository.findOneSeasonTestAverage(year, grade, season);
		// 生徒のクラスのテスト情報
		List<OneSeasonTestAverageView> classTestList = userTestRepository.findOneSeasonTestAverage(year, grade,
				studentClazz.name(), season);

		log.info("gradeTestList: {}", gradeTestList);
		log.info("classTestList: {}", classTestList);
		// タイトル設定
		String title = year + "年度の" + graphConfig.getGradeOption().getGrade() + "学年の"
				+ graphConfig.getSeasonOption().get(0).getSeasonName() + "の試験結果";
		graphData.setTitle(title);
		// ラベル設定
		graphConfig.getSubjectOption().valuesExcludeAll().stream()
				.forEach(subject -> graphData.addLabelName(subject.getSubjectName()));

		String fullName = studentTestList.stream().map(student -> student.getLastName() + " " + student.getFirstName())
				.findFirst().orElse("null");

		// 各データ名設定
		graphData.addUserName(fullName + "さんの試験結果");
		graphData.addUserName(studentClazz.getClazzName() + "の平均点");
		graphData.addUserName(graphConfig.getGradeOption().getGrade() + "学年の平均点");

		List<Integer> studentPoints = studentTestList.stream().map(s -> s.getUserPoint().intValue())
				.collect(Collectors.toList());
		List<Integer> classPoints = classTestList.stream().map(s -> s.getAvgPoint().intValue())
				.collect(Collectors.toList());
		List<Integer> gradePoints = gradeTestList.stream().map(s -> s.getAvgPoint().intValue())
				.collect(Collectors.toList());

		// 各データ設定
		graphData.addPoints(studentPoints);
		graphData.addPoints(classPoints);
		graphData.addPoints(gradePoints);
		return graphData;
	}

	/**
	 * 教科が一つの場合のグラフデータ作成メソッド
	 * 
	 * @param studentId
	 * @param graphConfig
	 * @return
	 */
	private GraphData createOneSubjectAndOneOrMultipleSeason(Long studentId, StudentGraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		int grade = graphConfig.getGradeOption().getGrade();
		String subject = graphConfig.getSubjectOption().getSubjectName();
		List<String> seasons = graphConfig.getSeasonOption().stream().map(s -> s.getSeasonName())
				.collect(Collectors.toList());
		// 生徒のテスト情報
		List<OneStudentView> studentTestList = userTestRepository.findAnySeasonOneSubjectTestStudent(grade, studentId,
				subject, seasons);

		// 生徒の所属のクラス
		Clazz studentClazz = null;
		// 年度
		int year = 0;

		if (CollectionUtils.isEmpty(studentTestList)) {
			UserGradeClassView view = userGradeClassRepository.findUserGradeClassView(studentId, grade);
			if (view == null) {
				return null;
			}
			studentClazz = Clazz.getClazzEnum(view.getClazzName());
			year = view.getYear();
		} else {
			studentClazz = Clazz.getClazzEnum(studentTestList.get(0).getClazzName());
			year = studentTestList.get(0).getYear();
		}

		// 生徒の学年のテスト情報
		List<AnySeasonTestAverageView> gradeTestList = userTestRepository.findAnySeasonTestAverage(year, grade, subject,
				seasons);
		// 生徒のクラスのテスト情報
		List<AnySeasonTestAverageView> classTestList = userTestRepository.findAnySeasonTestAverage(year, grade,
				studentClazz.name(), subject, seasons);

		log.info("gradeTestList: {}", gradeTestList);
		log.info("classTestList: {}", classTestList);
		// タイトル設定
		String title = year + "年度の" + subject + "の試験結果";
		graphData.setTitle(title);
		// ラベル設定
		graphConfig.getSeasonOption().stream().forEach(season -> graphData.addLabelName(season.getSeasonName()));
		// 各データ名設定
		String fullName = studentTestList.stream().map(s -> s.getLastName() + " " + s.getFirstName()).findFirst()
				.orElse("null");
		graphData.addUserName(fullName + "さんの試験結果");
		graphData.addUserName(studentClazz.getClazzName() + "の平均点");
		graphData.addUserName(grade + "学年の平均点");
		// 各データ設定
		List<Integer> studentPoints = studentTestList.stream().map(s -> s.getUserPoint().intValue())
				.collect(Collectors.toList());
		List<Integer> classPoints = classTestList.stream().map(s -> s.getAvgPoint().intValue())
				.collect(Collectors.toList());
		List<Integer> gradePoints = gradeTestList.stream().map(s -> s.getAvgPoint().intValue())
				.collect(Collectors.toList());
		graphData.addPoints(studentPoints);
		graphData.addPoints(classPoints);
		graphData.addPoints(gradePoints);
		return graphData;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/**
	 * 教師用のグラフデータを返却するメソッド
	 */
	@Override
	public GraphData createGradeGraphData(GraphConfig graphConfig) {
		// グラフのシーズン状態が空の場合は全て選択しているとみなすため、全てのSeasonオプションを追加
		if (graphConfig.getSeasonOption().isEmpty()) {
			Arrays.stream(Season.values()).forEach(season -> {
				graphConfig.addSeasonOption(season);
			});
		}
		// グラフの教科状態が空の場合は全てを選択しているものとする
		if (Objects.isNull(graphConfig.getSubjectOption())) {
			graphConfig.setSubjectObtionSubjectAll();
		}
		boolean allClass = graphConfig.getClazzOption() == Clazz.ALL;
		boolean allSubject = graphConfig.getSubjectOption() == Subject.ALL;
		boolean multipleSeason = graphConfig.getSeasonOption().size() > 1;

		// クラスが単体・テスト時期が単体・教科が全て
		if (!allClass && !multipleSeason && allSubject) {
			return createOneClassAndOneSeasonAndAllSubject(graphConfig);
		}
		// クラスが単体・教科が単体・テスト時期が2以上
		if (!allClass && !allSubject) {
			return createOneClassAndOneSubjectGraphData(graphConfig);
		}
		// クラスが単体・教科が全て・テスト時期が2以上
		if (!allClass && allSubject) {
			return createOneClassAndAllSubjectGraphData(graphConfig);
		}
		// クラスが全て・教科が単体・テスト時期が2以上
		if (allClass && !allSubject) {
			return createAllClassAndOneOrMultipleSeasonAndOneSubjectGraphData(graphConfig);
		}
		// 全クラス・全教科・テスト時期が2以上
		if (allClass && allSubject) {
			return createAllClassAndAllSubjectGraphData(graphConfig);
		}
		return null;
	}

	// 特殊でないグラフ、一つのクラスと教科が一つの場合のデータ返却用 (クラスが単体・教科が単体・時期が複数)
	private GraphData createOneClassAndOneSubjectGraphData(GraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		// タイトルの設定
		String title = graphConfig.getYear() + "年度の" + graphConfig.getGradeOption().getGrade() + "学年の"
				+ graphConfig.getSubjectOption().getSubjectName() + "の試験結果";
		graphData.setTitle(title);
		// ラベルの設定
		graphConfig.getSeasonOption().stream().forEach(season -> graphData.addLabelName(season.getSeasonName()));
		// データ名設定
		graphData.addUserName(graphConfig.getClazzOption().getClazzName() + "の平均");
		graphData.addUserName(graphConfig.getGradeOption().getGrade() + "学年全体の平均");

		List<String> subjects = graphConfig.getSeasonOption().stream().map(s -> s.getSeasonName())
				.collect(Collectors.toList());

		// 単体のクラス
		List<AnySeasonTestAverageView> oneClassStudentTests = userTestRepository.findAnySeasonTestAverage(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(),
				graphConfig.getClazzOption().name(), graphConfig.getSubjectOption().getSubjectName(), subjects);
		// 学年全体
		List<AnySeasonTestAverageView> allClassStudentTests = userTestRepository.findAnySeasonTestAverage(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(),
				graphConfig.getSubjectOption().getSubjectName(), subjects);
		// 単体クラスの平均点リスト
		List<Integer> oneClassPointList = oneClassStudentTests.stream().map(p -> p.getAvgPoint().intValue())
				.collect(Collectors.toList());
		// 学年の平均点リスト
		List<Integer> gradeClassPointList = allClassStudentTests.stream().map(p -> p.getAvgPoint().intValue())
				.collect(Collectors.toList());

		graphData.addPoints(oneClassPointList);
		graphData.addPoints(gradeClassPointList);
		return graphData;
	}

	/**
	 * 一つのクラスと全教科が選ばれた場合に呼び出されるメソッド // クラスが単体・教科が全て・テスト時期が2以上
	 * 
	 * @param graphConfig
	 * @return
	 */
	private GraphData createOneClassAndAllSubjectGraphData(GraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		// タイトル設定
		String title = graphConfig.getYear() + "年度の" + graphConfig.getGradeOption().getGrade() + "学年の"
				+ graphConfig.getSubjectOption().getSubjectName() + "の試験結果";
		graphData.setTitle(title);
		// ラベル設定
		graphConfig.getSeasonOption().stream().forEach(season -> graphData.addLabelName(season.getSeasonName()));
		// データ名設定
		graphData.addUserName(graphConfig.getClazzOption().getClazzName() + "の全教科合計平均");
		graphData.addUserName(graphConfig.getGradeOption().getGrade() + "学年全体の全教科合計平均");

		List<String> seasons = graphConfig.getSeasonOption().stream().map(s -> s.getSeasonName())
				.collect(Collectors.toList());
		// データ
		// 単体のクラス
		List<AnySeasonAndAllSubjectView> oneClassStudentTests = userTestRepository.findAnySeasonAndAllSubjects(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(),
				graphConfig.getClazzOption().name(), seasons);
		// 学年全体
		List<AnySeasonAndAllSubjectView> gradeStudentTests = userTestRepository.findAnySeasonAndAllSubjects(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(), seasons);
		// 一つのクラスの合計平均
		List<Integer> oneClassSumAverage = oneClassStudentTests.stream().map(p -> p.getAvgPoint().intValue())
				.collect(Collectors.toList());
		graphData.addPoints(oneClassSumAverage);
		// 学年全体の合計平均
		List<Integer> gradeSumAverage = gradeStudentTests.stream().map(p -> p.getAvgPoint().intValue())
				.collect(Collectors.toList());
		graphData.addPoints(gradeSumAverage);
		return graphData;
	}

	/**
	 * クラスが全て、教科が一つの場合のグラフデータを作成し、返却するメソッド // クラスが全て・教科が単体・テスト時期が2以上 //
	 * クラスごとの単教科の平均点 // クラスごとの単教科の平均点
	 * 
	 * @param graphConfig
	 * @return
	 */
	private GraphData createAllClassAndOneOrMultipleSeasonAndOneSubjectGraphData(GraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		// タイトル設定
		String title = graphConfig.getYear() + "年度の" + graphConfig.getGradeOption().getGrade() + "学年の全クラスの"
				+ graphConfig.getSubjectOption().getSubjectName() + "の試験結果";
		graphData.setTitle(title);
		// ラベル設定
		graphConfig.getSeasonOption().stream().forEach(season -> graphData.addLabelName(season.getSeasonName()));
		// データ名設定
		graphConfig.getClazzOption().valuesExcludeAll()
				.forEach(clazz -> graphData.addUserName(clazz.getClazzName() + "の平均点"));
		// 学年全体のテストリスト
		List<String> seasons = graphConfig.getSeasonOption().stream().map(s -> s.getSeasonName())
				.collect(Collectors.toList());
		List<AllClassOneSubjectView> gradeStudentTests = userTestRepository.findAllClassOneSubjects(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(),
				graphConfig.getSubjectOption().getSubjectName(), seasons);

		for (Clazz clazz : Clazz.valuesExcudeAllArray()) {
			List<Integer> pointList = new ArrayList<>();
			for (AllClassOneSubjectView view : gradeStudentTests) {
				if (clazz.name().equals(view.getClazzName())) {
					// クラス名が等しい
					pointList.add(view.getAvgPoint().intValue());
				}
			}
			graphData.addPoints(pointList);
		}

		return graphData;
	}

	/**
	 * 全クラス、全教科を選択された場合のグラフデータ作成 // 全クラス・全教科・テスト時期が2以上
	 * 
	 * @param graphConfig
	 * @return
	 */
	private GraphData createAllClassAndAllSubjectGraphData(GraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		// タイトル設定
		String title = graphConfig.getYear() + "年度の" + graphConfig.getGradeOption().getGrade() + "学年の"
				+ graphConfig.getSubjectOption().getSubjectName() + "の試験結果";
		graphData.setTitle(title);
		// ラベル名設定
		graphConfig.getSeasonOption().stream().forEach(season -> graphData.addLabelName(season.getSeasonName()));
		// データ名設定
		graphConfig.getClazzOption().valuesExcludeAll()
				.forEach(clazz -> graphData.addUserName(clazz.getClazzName() + "の合計平均"));
		// 学年全体のテストリスト
		List<String> seasons = graphConfig.getSeasonOption().stream().map(s -> s.getSeasonName())
				.collect(Collectors.toList());
		List<AllClassAndSubjectView> gradeStudentTests = userTestRepository.findAllClassAndSubjects(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(), seasons);

		for (Clazz clazz : Clazz.valuesExcudeAllArray()) {
			List<Integer> pointList = new ArrayList<>();
			for (AllClassAndSubjectView view : gradeStudentTests) {
				if (clazz.name().equals(view.getClazzName())) {
					pointList.add(view.getAvgPoint().intValue());
				}
			}
			graphData.addPoints(pointList);
		}

		return graphData;
	}

	/**
	 * 特殊グラフデータ、一つのクラス・一つの時期・全教科が選択された場合のグラフデータを返却 クラスオプションが単体・テスト時期が単体・教科が全て
	 * 
	 * @param graphConfig
	 * @return
	 */
	private GraphData createOneClassAndOneSeasonAndAllSubject(GraphConfig graphConfig) {
		GraphData graphData = new GraphData();
		// タイトル設定
		String title = graphConfig.getYear() + "年度の" + graphConfig.getGradeOption().getGrade() + "学年の"
				+ graphConfig.getSeasonOption().get(0).getSeasonName() + "の試験結果";
		graphData.setTitle(title);
		// ラベル設定
		graphConfig.getSubjectOption().valuesExcludeAll().stream().map(s -> s.getSubjectName())
				.sorted(String::compareTo).forEach(subject -> graphData.addLabelName(subject));
		// データ名設定
		graphData.addUserName(graphConfig.getClazzOption().getClazzName() + "の平均点");
		graphData.addUserName(graphConfig.getGradeOption().getGrade() + "学年全体の平均点");
		// 1のクラスのテストリスト
		List<OneSeasonTestAverageView> oneClassTestLists = userTestRepository.findOneSeasonTestAverage(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(),
				graphConfig.getClazzOption().name(), graphConfig.getSeasonOption().get(0).getSeasonName());
		// 学年全体のテストリスト
		List<OneSeasonTestAverageView> allClassTestLists = userTestRepository.findOneSeasonTestAverage(
				graphConfig.getYear(), graphConfig.getGradeOption().getGrade(),
				graphConfig.getSeasonOption().get(0).getSeasonName());
		// 1つのクラスの教科ごとの平均点
		List<Integer> oneClassAveragePoint = oneClassTestLists.stream().map(p -> Math.round(p.getAvgPoint().intValue()))
				.collect(Collectors.toList());
		graphData.addPoints(oneClassAveragePoint);
		// 学年全体の教科ごとの平均点
		List<Integer> allClassAveragePoint = allClassTestLists.stream().map(p -> Math.round(p.getAvgPoint().intValue()))
				.collect(Collectors.toList());
		graphData.addPoints(allClassAveragePoint);
		return graphData;
	}

	/**
	 * 標準偏差用グラフデータを作成し、返却するメソッド
	 */
	@Override
	public DeviationGraphData createDeviationGraphData(DeviationGraphConfig deviationGraphConfig) {
		if (!deviationGraphConfig.allFieldNotNull()) {
			// 未選択のフィールドが存在する
			return null;
		}

		List<Integer> pointList = userTestRepository.findAllPoints(deviationGraphConfig.getYear(),
				deviationGraphConfig.getGradeOption().getGrade(),
				deviationGraphConfig.getSeasonOption().getSeasonName(),
				deviationGraphConfig.getSubjectOption().getSubjectName());
		// タイトル設定
		String title = deviationGraphConfig.getYear() + "年度の" + deviationGraphConfig.getGradeOption().getGrade() + "学年"
				+ deviationGraphConfig.getSeasonOption().getSeasonName()
				+ deviationGraphConfig.getSubjectOption().getSubjectName() + "の標準偏差";
//
		return createDeviationGraphDataHelper(pointList, title);
	}

	private DeviationGraphData createDeviationGraphDataHelper(List<Integer> pointList, String title) {
		// pointListが空の場合の処理を考える
		// とりあえず適当なデータを使って返却
		if (CollectionUtils.isEmpty(pointList)) {
			DeviationGraphData temporaryGraphData = new DeviationGraphData();
			for (int i = 0; i <= 100; i += 10) {
				StandardDeviation temporaryData = StandardDeviation.builder().point(i).numberOfPeaple(0).build();
				temporaryGraphData.addStandardDeviation(temporaryData);
			}
			temporaryGraphData.setLabel(title);
			return temporaryGraphData;
		}
		// テストの平均点
		int averagePoint = pointList.stream().collect(Collectors.averagingInt(point -> point)).intValue();
		// テストの偏差
		List<Double> deviationList = pointList.stream().map(point -> Math.pow(point - averagePoint, 2))
				.collect(Collectors.toList());
		// 分散を求める
		Double distributeValue = deviationList.stream().collect(Collectors.summingDouble(deviation -> deviation))
				/ pointList.size();
		// 偏差値を求める
		int deviationValue = (int) Math.sqrt(distributeValue);
		List<Integer> randomVariableList = new ArrayList<>();
		for (int i = averagePoint; true; i -= deviationValue) {
			if (i <= 0) {
				randomVariableList.add(0);
				break;
			}
			randomVariableList.add(i);
		}
		for (int i = averagePoint + deviationValue; true; i += deviationValue) {
			if (i >= 100) {
				randomVariableList.add(100);
				break;
			}
			randomVariableList.add(i);
		}
		randomVariableList.sort(Comparator.naturalOrder());
		// 返却するグラフデータ
		DeviationGraphData deviationGraphData = new DeviationGraphData();

		deviationGraphData.setLabel(title);
		// 返却するグラフデータのデータ部分を作成
		StandardDeviation first = StandardDeviation.builder().point(0).numberOfPeaple(0).build();
		deviationGraphData.addStandardDeviation(first);
		for (int i = 0; i < randomVariableList.size(); i++) {
			int startVal = randomVariableList.get(i);
			int endVal = randomVariableList.get(i + 1);
			if (endVal != 100) {
				int numOfPeaple = (int) pointList.stream().filter(point -> startVal <= point && point < endVal).count();
				StandardDeviation addStandardDeviation = StandardDeviation.builder().point(endVal)
						.numberOfPeaple(numOfPeaple).build();
				deviationGraphData.addStandardDeviation(addStandardDeviation);
			} else {
				int numOfPeaple = (int) pointList.stream().filter(point -> startVal <= point && point <= endVal)
						.count();
				StandardDeviation addStandardDeviation = StandardDeviation.builder().point(endVal)
						.numberOfPeaple(numOfPeaple).build();
				deviationGraphData.addStandardDeviation(addStandardDeviation);
				break;
			}
		}
		return deviationGraphData;
	}

	@Override
	public DeviationGraphData createStudentDeviationGraphData(StudentDeviationGraphConfig studentDeviationGraphConfig,
			Long studentId) {
		if (!studentDeviationGraphConfig.allFieldNotNull()) {
			return null;
		}
		int grade = studentDeviationGraphConfig.getGradeOption().getGrade();
		String season = studentDeviationGraphConfig.getSeasonOption().getSeasonName();
		String subject = studentDeviationGraphConfig.getSubjectOption().getSubjectName();

		Integer year = userTestRepository.findYearOfTestByStudentInfo(studentId, grade, season, subject);

		List<Integer> pointList = userTestRepository.findAllPoints(year, grade, season, subject);

		StringBuilder sb = new StringBuilder();
		// タイトル設定
		String title = sb.append(year).append("年度の").append(grade).append("学年").append(season).append(subject)
				.append("の標準偏差").toString();
		return createDeviationGraphDataHelper(pointList, title);
	}

}
