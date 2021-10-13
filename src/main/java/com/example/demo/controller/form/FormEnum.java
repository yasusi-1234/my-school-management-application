package com.example.demo.controller.form;

import static com.example.demo.common.SchoolCommonValue.CLASSNAME;
import static com.example.demo.common.SchoolCommonValue.CLASS_NAME;
import static com.example.demo.common.SchoolCommonValue.GRADE;
import static com.example.demo.common.SchoolCommonValue.POINT;
import static com.example.demo.common.SchoolCommonValue.SEASON_NAME;
import static com.example.demo.common.SchoolCommonValue.SUBJECT_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.StringUtils;

import com.google.common.base.Objects;

import lombok.Getter;

public final class FormEnum {

	@Getter
	public enum Grade {
		ONE(1), TWO(2), THREE(3);

		private Grade(int grade) {
			this.grade = grade;
		}

		private int grade;

		public String getHeaderName() {
			return GRADE;
		}

		public static Grade gradeEnum(int gradeValue) {
			for (Grade g : Grade.values()) {
				if (g.getGrade() == gradeValue) {
					return g;
				}
			}
			return null;
		}

		/**
		 * 指定された学年の数値と一致するGradeを返却、存在しない場合はNull
		 * 
		 * @param gradeValue
		 * @return
		 */
		public static Grade getMatchGrade(int gradeValue) {
			for (Grade g : Grade.values()) {
				if (g.getGrade() == gradeValue) {
					return g;
				}
			}
			return null;
		}
	}

	@Getter
	public enum Clazz {
		A("A組"), B("B組"), C("C組"), D("D組"), E("E組"), ALL("全て");

		private Clazz(String clazzName) {
			this.clazzName = clazzName;
		}

		private String clazzName;

		public String getHeaderName() {
			return CLASS_NAME;
		}

		public static Clazz getClazzEnum(String clazzName) {
			if (!StringUtils.hasText(clazzName)) {
				return null;
			}
			for (Clazz clazz : Clazz.values()) {
				if (clazz.name().equals(clazzName) || clazz.getClazzName().equals(clazzName)) {
					return clazz;
				}
			}
			return null;
		}

		public static Clazz[] valuesExcudeAllArray() {
			Clazz[] array = Clazz.values();
			Clazz[] returnArray = new Clazz[array.length - 1];
			int cnt = 0;
			for (Clazz c : array) {
				if (c == Clazz.ALL) {
					continue;
				}
				returnArray[cnt] = c;
				cnt++;
			}
			return returnArray;
		}

		/**
		 * ClazzのALLを除いたClazzリスト
		 * 
		 * @return
		 */
		public List<Clazz> valuesExcludeAll() {
			List<Clazz> list = new ArrayList<>(Arrays.asList(Clazz.values()));
			int index = list.indexOf(ALL);
			list.remove(index);
			return list;
		}
	}

	@Getter
	public enum Season {
		MIDDLE_FIRST_SEMESTER("1学期中間", 1), END_FIRST_SEMESTER("1学期期末", 2), MIDDLE_SECOND_SEMESTER("2学期中間", 3),
		END_SECOND_SEMESTER("2学期期末", 4), MIDDLE_THIRD_SEMESTER("3学期中間", 5), END_THIRD_SEMESTER("3学期期末", 6);
//		, ALL("全期間");

		private Season(String seasonName, int index) {
			this.seasonName = seasonName;
			this.index = index;
		}

		private String seasonName;
		private int index;

		/**
		 * Seasonの所持しているSeasonNameが一致したSeason情報を返却する
		 * 
		 * @param seasonName テスト時期名
		 * @return
		 */
		public static Season getSeasonBySeasonName(String seasonName) {
			if (StringUtils.hasText(seasonName)) {
				// 引数の値が空・Nullでない
				for (Season s : Season.values()) {
					if (s.getSeasonName().equals(seasonName)) {
						// 引数の値をSeasonEnumの所持するseasonNameが一致した
						return s;
					}
				}
			}

			return Season.MIDDLE_FIRST_SEMESTER;
		}

		public String getHeaderName() {
			return SEASON_NAME;
		}

		/**
		 * 指定された値と等しい(seasonNameと)SeasonのEnum要素を返却する
		 * 
		 * @param seasonName
		 * @return
		 */
		public static Season seasonEnum(String seasonName) {
			if (StringUtils.hasText(seasonName)) {
				for (Season season : Season.values()) {
					if (Objects.equal(season.getSeasonName(), seasonName)) {
						return season;
					}
				}
			}
			return null;
		}

//		public List<Season> valuesExcludeAll() {
//			List<Season> list = new ArrayList<>(Arrays.asList(Season.values()));
//			int index = list.indexOf(ALL);
//			list.remove(index);
//			return list;
//		}
	}

	@Getter
	public enum Subject {
		MATH("数学"), ENGLISH("英語"), LANGUAGE("国語"), CHEMISTRY("理科"), SOCIETY("社会"), GEOGRAPHY("地理"), ALL("全教科");

		private Subject(String subjectName) {
			this.subjectName = subjectName;
		}

		private String subjectName;

		public String getHeaderName() {
			return SUBJECT_NAME;
		}

		public static Subject[] valuesExcludeAllArray() {
			Subject[] subjects = Subject.values();
			Subject[] returnArray = new Subject[subjects.length - 1];
			int cnt = 0;
			for (Subject s : subjects) {
				if (s == Subject.ALL) {
					continue;
				}
				returnArray[cnt] = s;
				cnt++;
			}
			return returnArray;
		}

		/**
		 * SubjectのALLを除いたSubjectリスト
		 * 
		 * @return
		 */
		public List<Subject> valuesExcludeAll() {
			List<Subject> list = new ArrayList<>(Arrays.asList(Subject.values()));
			int index = list.indexOf(ALL);
			list.remove(index);
			return list;
		}
	}

	@Getter
	public enum SortPoint {
		NONE("指定なし") {
			@Override
			public Sort getSort() {
				return Sort.unsorted();
			}
		},
		ASC("昇順") {
			@Override
			public Sort getSort() {
				return Sort.by(Direction.ASC, POINT);
			}
		},
		DESC("降順") {
			@Override
			public Sort getSort() {
				return Sort.by(Direction.DESC, POINT);
			}
		};

		private SortPoint(String textVal) {
			this.textVal = textVal;
		}

		private String textVal;

		public abstract Sort getSort();
	}

	@Getter
	public enum SortClass {
		NONE("指定なし") {
			@Override
			public Sort getSort() {
				return Sort.unsorted();
			}
		},
		ASC("昇順") {
			@Override
			public Sort getSort() {
				return Sort.by(Direction.ASC, CLASSNAME);
			}
		},
		DESC("降順") {
			@Override
			public Sort getSort() {

				return Sort.by(Direction.DESC, CLASSNAME);
			}
		};

		private SortClass(String textVal) {
			this.textVal = textVal;
		}

		private String textVal;

		public abstract Sort getSort();
	}

	@Getter
	public enum GraphType {
		RADAR("レーダーグラフ"), BAR("棒グラフ");

		GraphType(String textVal) {
			this.textVal = textVal;
		}

		private String textVal;
	}

	@Getter
	public enum RegistrationData {
		DATA_STUDENT("学生データ"), DATA_TEST_RESULT("生徒の試験結果");
//		, TEST_INFOMATION("試験概要");

		RegistrationData(String dataType) {
			this.dataType = dataType;
		}

		private String dataType;

		public String getDataType() {
			return this.dataType;
		}
	}

	@Getter
	public enum DBInjectType {
		NEW_INSERT("新規挿入"), DB_UPDATE("更新");

		private DBInjectType(String injectType) {
			this.injectType = injectType;
		}

		private String injectType;
	}

	@Getter
	public enum NumDisplay {
		VERY_SMALL(10), SMALL(25), SMALL_MIDDLE(50), MIDDLE(75), MIDDLE_LARGE(100), LARGE(200), VERY_LARGE(500);

		private NumDisplay(int displayCount) {
			// TODO 自動生成されたコンストラクター・スタブ
			this.displayCount = displayCount;
		}

		private int displayCount;
	}

	@Getter
	public enum SortSumPoint {
		ASC("昇順") {

			@Override
			public Sort getSort() {
				return Sort.by(Order.asc("sumPoint"));
			}
		},
		DESC("降順") {

			@Override
			public Sort getSort() {
				return Sort.by(Order.desc("sumPoint"));
			}
		};

		SortSumPoint(String textVal) {
			this.textVal = textVal;
		}

		private String textVal;

		public abstract Sort getSort();
	}

}
