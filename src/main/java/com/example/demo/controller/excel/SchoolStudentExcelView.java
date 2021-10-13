package com.example.demo.controller.excel;

import static com.example.demo.common.SchoolCommonValue.FIRST_NAME;
import static com.example.demo.common.SchoolCommonValue.LAST_NAME;
import static com.example.demo.common.SchoolCommonValue.USER_ID;
import static com.example.demo.common.SchoolCommonValue.YEAR;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.example.demo.controller.form.CreateStudentExcelTemplateForm;
import com.example.demo.controller.form.FormEnum.Clazz;
import com.example.demo.domain.grade.model.UserGradeClass;

public class SchoolStudentExcelView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CreateStudentExcelTemplateForm form = (CreateStudentExcelTemplateForm) model.get("form");

		Sheet sheet = workbook.createSheet(form.getYear() + "年度" + form.getGrade().getGrade() + "学年");

		@SuppressWarnings("unchecked")
		List<UserGradeClass> studentGradeClassList = (List<UserGradeClass>) model.get("studentGradeClassList");
		// ヘッダーの生成
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(USER_ID);
		row.createCell(1).setCellValue(LAST_NAME);
		row.createCell(2).setCellValue(FIRST_NAME);
		row.createCell(3).setCellValue(YEAR);
		row.createCell(4).setCellValue(form.getGrade().getHeaderName());
		row.createCell(5).setCellValue(Clazz.ALL.getHeaderName());

		// ボディー作成
		for (int i = 0; i < studentGradeClassList.size(); i++) {
			UserGradeClass ugc = studentGradeClassList.get(i);
			row = sheet.createRow(i + 1);
			row.createCell(0).setCellValue(ugc.getAppUser().getUserId());
			row.createCell(1).setCellValue(ugc.getAppUser().getLastName());
			row.createCell(2).setCellValue(ugc.getAppUser().getFirstName());
			row.createCell(3).setCellValue(form.getYear());
			row.createCell(4).setCellValue(form.getGrade().getGrade());
		}

		// カラムのサイズ調整
		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}

	}

}
