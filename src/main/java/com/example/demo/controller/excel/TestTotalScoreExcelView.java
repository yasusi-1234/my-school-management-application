package com.example.demo.controller.excel;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.example.demo.controller.form.SumSurveyForm;
import com.example.demo.domain.test.model.propagation.UserTestView;

public class TestTotalScoreExcelView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SumSurveyForm form = (SumSurveyForm) model.get("sumSurveyForm");
		@SuppressWarnings("unchecked")
		List<UserTestView> resultSumTests = (List<UserTestView>) model.get("studentSumTestList");

		Sheet sheet = workbook.createSheet(form.getGrade().getGrade() + "学年" + form.getClazz().getClazzName());

		// ヘッダーの生成
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue("No");
		row.createCell(1).setCellValue("氏名");
		row.createCell(2).setCellValue("クラス");
		row.createCell(3).setCellValue("合計点");

		// ボディー作成
		for (int i = 0; i < resultSumTests.size(); i++) {
			UserTestView testResult = resultSumTests.get(i);
			row = sheet.createRow(i + 1);
			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(testResult.getLastName() + " " + testResult.getFirstName());
			row.createCell(2).setCellValue(testResult.getClazzName());
			row.createCell(3).setCellValue(testResult.getSumPoint());
		}

		// カラムのサイズ調整
		for (int i = 0; i < 4; i++) {
			sheet.autoSizeColumn(i);
		}
	}

}
