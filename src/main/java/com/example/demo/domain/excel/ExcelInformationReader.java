package com.example.demo.domain.excel;

import static com.example.demo.common.SchoolCommonValue.BR;
import static com.example.demo.common.SchoolCommonValue.CLASS_NAME;
import static com.example.demo.common.SchoolCommonValue.FIRST_NAME;
import static com.example.demo.common.SchoolCommonValue.GRADE;
import static com.example.demo.common.SchoolCommonValue.LAST_NAME;
import static com.example.demo.common.SchoolCommonValue.POINT;
import static com.example.demo.common.SchoolCommonValue.SEASON_NAME;
import static com.example.demo.common.SchoolCommonValue.SUBJECT_NAME;
import static com.example.demo.common.SchoolCommonValue.USER_ID;
import static com.example.demo.common.SchoolCommonValue.USER_NAME;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Component;

import com.example.demo.controller.form.FormEnum.DBInjectType;
import com.example.demo.controller.form.FormEnum.RegistrationData;
import com.example.demo.domain.excel.exception.PointAbnormalException;
import com.example.demo.domain.excel.exception.RequestDataBodyNotFoundException;
import com.example.demo.domain.excel.exception.RequiredBodyValueNotExistException;
import com.example.demo.domain.excel.exception.RequiredHeaderValueNotExistException;

@Component
public class ExcelInformationReader {

	public List<Map<String, String>> analyzeExcelFile(InputStream in, NecessaryInformation information) {

		// 返却用ListMap
		List<Map<String, String>> returnMap = information.returnModelMap();
		try (Workbook workbook = WorkbookFactory.create(in)) {

			Sheet sheet = workbook.getSheetAt(0);
			// 最初の行
			int firstRow = sheet.getFirstRowNum();
			// 最後の行
			int lastRow = rowExistPosition(sheet);

			Row row = sheet.getRow(firstRow);
			// 最初の列
			int firstColumn = row.getFirstCellNum();
			// ヘッダーの位置を特定するためのMap
			Map<String, Integer> targetMap = information.getNecessaryMap();

			for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
				// ヘッダーの値
				String header = getConversionCellValue(row.getCell(i));
				// ヘッダーの位置
				int index = row.getCell(i).getColumnIndex();
				targetMap.entrySet().stream().filter(entry -> entry.getKey().equals(header)).forEach(entry -> {
					// ヘッダーの値とtargetMapのKeyの値と一致した場合
					targetMap.put(entry.getKey(), index); // ヘッダーの位置を設定
				});
			}
			// ヘッダーの値に問題が無いかチェック
			checkHeader(targetMap);

			// RequiredBodyValueNotExistException通知用
			Map<Integer, List<String>> notExistMap = new HashMap<>();
			// PointAbnormalException通知用
			List<Integer> abnormalPointList = new ArrayList<>();

			for (int rowIndex = firstRow + 1; rowIndex <= lastRow; rowIndex++) {
				Row moveRow = sheet.getRow(rowIndex);
				Map<String, String> returnAddMap = new HashMap<>();
				// Exception用
				List<String> notExistList = new ArrayList<>();
				for (int columnIndex = firstColumn; columnIndex < moveRow.getLastCellNum(); columnIndex++) {

					// マッピングした位置と同じ場合はデータを挿入する処理をする
					if (targetMap.containsValue(columnIndex)) {
						String keyName = getKey(targetMap, columnIndex);
						// この値が空だった場合はRequiredBodyValueNotExistException通知用のリストに値を挿入
						String cellValue = getConversionCellValue(moveRow.getCell(columnIndex));
						if (cellValue == null || cellValue.isBlank()) {
							notExistList.add(CellReference.convertNumToColString(columnIndex));
						}

						if (Objects.equals(keyName, POINT)) {
							// 数値に変換できない、点数が0未満100より上は異常値と見なし、条件が成立した場合は行番号を追加
							if (!NumberUtils.isCreatable(cellValue) || NumberUtils.toInt(cellValue) > 100
									|| NumberUtils.toInt(cellValue) < 0) {
								abnormalPointList.add(rowIndex + 1);

							}
						}
						returnAddMap.put(keyName, cellValue);
					}
				}
				if (!notExistList.isEmpty()) {
					notExistMap.put(rowIndex + 1, notExistList);
				}
				returnMap.add(returnAddMap);
			}

			// 値が存在しないセルがあった場合は、その情報を元に例外を投げる
			if (!notExistMap.isEmpty()) {
				throw new RequiredBodyValueNotExistException("値が挿入されていないセルが存在します。", notExistMap);
			}

			// 得点に異常のあるセルが存在した場合は、その情報を元に例外を投げる
			if (!abnormalPointList.isEmpty()) {
				StringBuilder sb = new StringBuilder("点数に異常な値が見つかりました").append(BR).append("異常が見つかった行： ");
				String abnormalLine = abnormalPointList.stream().map(index -> index.toString())
						.collect(Collectors.joining(", "));
				sb.append(abnormalLine).append(BR).append("上記の行に異常があります。確認の上修正してください");
				throw new PointAbnormalException(sb.toString(), abnormalPointList);
			}

			// リクエストされたExcel情報のData部分に内も入力が無かった場合に送出
			if (returnMap.isEmpty()) {
				throw new RequestDataBodyNotFoundException("Excelヘッダーに対応したデータが入力されていません");
			}

		} catch (EncryptedDocumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return returnMap;
	}

	/**
	 * 取得したExcelのセル情報から値を取得し、String値に変換して返却する
	 * 
	 * @param cell セル情報
	 * @return セル情報から得た内容からString値にparseした値
	 */
	private static String getConversionCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case NUMERIC:
			int returnVal = (int) cell.getNumericCellValue();
			return String.valueOf(returnVal);
		case STRING:
			return cell.getStringCellValue();
		case BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			case _NONE:
			case ERROR:
				return "";
		}
		return "";
	}

	/**
	 * Poiの仕様上sheet.getLastRowNumメソッドは、
	 * Excelの行に書き込みを行いそのあとその行をクリアした場合でも、存在する行としてカウントしてしまう
	 * バグの原因となってしまうので、実際に書き込みのある行を調べた上で返却を行うためのメソッド
	 * 
	 * @param sheet シート情報
	 * @return Excelに挿入されている情報がある最終行の位置
	 */
	private int rowExistPosition(Sheet sheet) {
		// poiにより算出された最終行
		int rowNumOfPoi = sheet.getLastRowNum();
		Row row = sheet.getRow(rowNumOfPoi);
		// poiにより算出された最終列
		int columnNumOfPoi = row.getLastCellNum();
		// 返却値
		int returnVal = rowNumOfPoi;
		for (int indexRow = rowNumOfPoi; true; indexRow--) {
			Row moveRow = sheet.getRow(indexRow);
			// ループのフラグ
			boolean breakFlag = false;
			for (int columnIndex = 0; columnIndex < columnNumOfPoi; columnIndex++) {
				String cellValue = getConversionCellValue(moveRow.getCell(columnIndex));
				if (!cellValue.isBlank()) {
					breakFlag = true;
					returnVal = indexRow;
					break;
				}
			}
			if (breakFlag) {
				break;
			}
		}
		return returnVal;
	}

	/**
	 * キー情報を値(Value)から取得し返却する
	 * 
	 * @param map サーチ元のマップ
	 * @param num 取得したいKeyのValue情報
	 * @return キー情報
	 */
	private String getKey(Map<String, Integer> map, int num) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue().equals(num)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * ヘッダーマップ(value)の値に-1が入っているものが無いかチェックして、格納されていた場合は例外を投げるメソッド
	 * 
	 * @param checkMap チェックするMap情報
	 * @throws RequiredHeaderValueNotExistException チェック用のマップに必要な情報が無かった場合に投げられる
	 */
	private void checkHeader(Map<String, Integer> checkMap) {
		List<String> requiredHeaderList = checkMap.entrySet().stream().filter(entry -> entry.getValue() == -1)
				.map(Entry::getKey).collect(Collectors.toList());
		// 値が存在しない物があった場合には例外を投げる
		if (!requiredHeaderList.isEmpty()) {
			StringBuilder sb = new StringBuilder("必要なヘッダーがみつかりません。見つからなかったヘッダー値: ");
			sb.append(requiredHeaderList.stream().collect(Collectors.joining(", ", "【", "】")));
			throw new RequiredHeaderValueNotExistException(sb.toString(), requiredHeaderList);
		}
	}

//	 implements Operation
	public enum NecessaryInformation {
		STUDENT_UPDATE {
			@Override
			public Map<String, Integer> getNecessaryMap() {
				Map<String, Integer> map = new LinkedHashMap<String, Integer>();
				map.put(USER_ID, -1);
				map.put(USER_NAME, -1);
				map.put(FIRST_NAME, -1);
				map.put(LAST_NAME, -1);
				return map;
			}

		},
		GRADE_CLASS_ONLY {
			@Override
			public Map<String, Integer> getNecessaryMap() {
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put(GRADE, -1);
				map.put(CLASS_NAME, -1);
				map.put(YEAR, -1);
				return map;
			}
		},
		STUDENT_GRADE_CLASS_UPDATE {
			@Override
			public Map<String, Integer> getNecessaryMap() {
				Map<String, Integer> map = new LinkedHashMap<String, Integer>();
				map.put(USER_ID, -1);
				map.put(USER_NAME, -1);
				map.put(FIRST_NAME, -1);
				map.put(LAST_NAME, -1);
				map.put(GRADE, -1);
				map.put(CLASS_NAME, -1);
				map.put(YEAR, -1);
				return map;
			}
		},
		STUDENT_GRADE_CLASS_INSERT {
			@Override
			public Map<String, Integer> getNecessaryMap() {
				Map<String, Integer> map = new LinkedHashMap<String, Integer>();
				map.put(USER_NAME, -1); // User_Nameは初期値として作成されていることを想定
				map.put(FIRST_NAME, -1);
				map.put(LAST_NAME, -1);
				map.put(GRADE, -1);
				map.put(CLASS_NAME, -1);
				map.put(YEAR, -1);
				return map;
			}
		},
		STUDENT_TEST {
			@Override
			public Map<String, Integer> getNecessaryMap() {
				Map<String, Integer> map = new LinkedHashMap<String, Integer>();
				map.put(USER_ID, -1);
				map.put(USER_NAME, -1);
				map.put(FIRST_NAME, -1);
				map.put(LAST_NAME, -1);
				map.put(CLASS_NAME, -1);
				map.put(SEASON_NAME, -1);
				map.put(SUBJECT_NAME, -1);
				map.put(YEAR, -1);
				map.put(GRADE, -1);
				map.put(POINT, -1);
				return map;
			}
		};

		public abstract Map<String, Integer> getNecessaryMap();

//		public abstract Map<String, Boolean> getCheckHeaderMap();

		public List<Map<String, String>> returnModelMap() {
			return new ArrayList<Map<String, String>>();
		}

	}

	/**
	 * registrationData(学生情報かテスト情報か)の情報とdbinjectType(新規挿入・更新)情報を元に
	 * 適合したNecessaryInformationの種類を返却する
	 * 
	 * @param registrationData 学生データか学生の試験データかのEnum値
	 * @param type 新規挿入か更新かのEnum値
	 * @return Enum情報から得た新たなEnum値(必要なMap情報を取得する為の)
	 */
	public NecessaryInformation getNecessaryInformation(RegistrationData registrationData, DBInjectType type) {
		if (registrationData == RegistrationData.DATA_STUDENT) {
			// データタイプが学生のデータ
			if (type == DBInjectType.DB_UPDATE) {
				// 更新の場合
				return NecessaryInformation.STUDENT_GRADE_CLASS_UPDATE;
			} else {
				// 新規挿入の場合
				return NecessaryInformation.STUDENT_GRADE_CLASS_INSERT;
			}
		} else {
			// データタイプがテスト結果の場合
			if (type == DBInjectType.DB_UPDATE) {
				// 更新の場合
				return NecessaryInformation.STUDENT_TEST;
			} else {
				// 新規挿入の場合
				return NecessaryInformation.STUDENT_TEST;
			}
		}
	}

}
