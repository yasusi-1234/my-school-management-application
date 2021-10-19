package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.domain.excel.exception.PointAbnormalException;
import com.example.demo.domain.excel.exception.RequestDataBodyNotFoundException;
import com.example.demo.domain.excel.exception.RequiredBodyValueNotExistException;
import com.example.demo.domain.excel.exception.RequiredHeaderValueNotExistException;
import com.example.demo.domain.school.exception.AlreadyRegisterDataException;
import com.example.demo.domain.school.exception.AppUserNotRegisterException;
import com.example.demo.domain.school.exception.ExcelDataException;
import com.example.demo.domain.school.exception.GradeClassNotRegisterException;
import com.example.demo.domain.school.exception.TestInformationNotRegisterException;

@ControllerAdvice
public class SchoolControllerAdvice {

	@ExceptionHandler
	public String gradeClassNotRegisterExceptionHandler(GradeClassNotRegisterException ex, Model model) {
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String alreadyRegisterDataExceptionHandler(AlreadyRegisterDataException ex, Model model) {
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String testInformationNotRegisterExceptionHandler(TestInformationNotRegisterException ex, Model model) {
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String appUserNotRegisterExceptionHandler(AppUserNotRegisterException ex, Model model) {
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String excelDataExceptionHandler(ExcelDataException ex, Model model) {
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String pointAbnormalExceptionHandler(PointAbnormalException ex, Model model) {
		model.addAttribute("cause", ex.getAbnormalPointIndex());
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String requiredBodyValueNotExistExceptionHandler(RequiredBodyValueNotExistException ex, Model model) {
//		model.addAttribute("cause", ex.getAbnormalPointIndex());
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		model.addAttribute("notExistMap", ex.getConvertNotExistMap());
		return "/error/excel-error";
	}

	@ExceptionHandler
	public String requestDataBodyNotFoundExceptionHandler(RequestDataBodyNotFoundException ex, Model model) {
//		model.addAttribute("cause", ex.getAbnormalPointIndex());
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String requiredHeaderValueNotExistExceptionHandler(RequiredHeaderValueNotExistException ex, Model model) {
//		model.addAttribute("cause", ex.getAbnormalPointIndex());
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}

	@ExceptionHandler
	public String httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex, Model model) {
//		model.addAttribute("cause", ex.getAbnormalPointIndex());
		model.addAttribute("status", HttpStatus.BAD_REQUEST);
		model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		model.addAttribute("timestamp", LocalDateTime.now().withNano(0));
		model.addAttribute("message", ex.getMessage());
		return "/error/400";
	}
}
