package com.example.demo.controller.form.validator;

import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ListNoNullValidation implements ConstraintValidator<ListNoNull, List<?>> {

	private String message;

	@Override
	public void initialize(ListNoNull constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(List<?> value, ConstraintValidatorContext context) {
		if (Objects.isNull(value) || value.isEmpty()) {
			return false;
		}
		return true;
	}

}
