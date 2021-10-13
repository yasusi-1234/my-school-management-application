package com.example.demo.controller.form.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileValidator implements ConstraintValidator<UploadFile, MultipartFile> {

	private String message;

	@Override
	public void initialize(UploadFile constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		if (file != null && StringUtils.hasLength(file.getOriginalFilename())
				&& (Objects.equals(file.getContentType(),
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
						|| Objects.equals(file.getContentType(), "application/vnd.ms-excel"))) {

			return true;
		}
		return false;
	}

}
