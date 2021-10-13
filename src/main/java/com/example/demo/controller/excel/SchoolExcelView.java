package com.example.demo.controller.excel;

import static com.example.demo.common.SchoolCommonValue.FIRST_NAME;
import static com.example.demo.common.SchoolCommonValue.LAST_NAME;
import static com.example.demo.common.SchoolCommonValue.POINT;
import static com.example.demo.common.SchoolCommonValue.USER_ID;
import static com.example.demo.common.SchoolCommonValue.USER_NAME;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.example.demo.controller.form.CreateExcelTemplateForm;
import com.example.demo.domain.grade.model.UserGradeClass;

public class SchoolExcelView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CreateExcelTemplateForm form = (CreateExcelTemplateForm) model.get("form");

		Sheet sheet = workbook.createSheet(form.getSubject().getHeaderName());

		@SuppressWarnings("unchecked")
		List<UserGradeClass> studentGradeClassList = (List<UserGradeClass>) model.get("studentGradeClassList");
		System.out.println(studentGradeClassList);
		// ヘッダーの生成
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(USER_ID);
		row.createCell(1).setCellValue(USER_NAME);
		row.createCell(2).setCellValue(LAST_NAME);
		row.createCell(3).setCellValue(FIRST_NAME);
		row.createCell(4).setCellValue(form.getGrade().getHeaderName());
		row.createCell(5).setCellValue(form.getClazz().getHeaderName());
		row.createCell(6).setCellValue(YEAR);
		row.createCell(7).setCellValue(form.getSeason().getHeaderName());
		row.createCell(8).setCellValue(form.getSubject().getHeaderName());
		row.createCell(9).setCellValue(POINT);

		// ボディー作成
		for (int i = 0; i < studentGradeClassList.size(); i++) {
			UserGradeClass ugc = studentGradeClassList.get(i);
			row = sheet.createRow(i + 1);
			row.createCell(0).setCellValue(ugc.getAppUser().getUserId());
			row.createCell(1).setCellValue(ugc.getAppUser().getUserName());
			row.createCell(2).setCellValue(ugc.getAppUser().getLastName());
			row.createCell(3).setCellValue(ugc.getAppUser().getFirstName());
			row.createCell(4).setCellValue(form.getGrade().getGrade());
			row.createCell(5).setCellValue(form.getClazz().name());
			row.createCell(6).setCellValue(form.getYear());
			row.createCell(7).setCellValue(form.getSeason().getSeasonName());
			row.createCell(8).setCellValue(form.getSubject().getSubjectName());
		}

		// カラムのサイズ調整
		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}
	}

}
