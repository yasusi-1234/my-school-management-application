package com.example.demo.controller.form.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoNullValidation implements ConstraintValidator<NoNull, Enum<?>> {

	private String message;

	@Override
	public void initialize(NoNull constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
//		BeanWrapper wrapper = new BeanWrapperImpl(value);
//		Enum<?> element = (Enum<?>) wrapper.getWrappedInstance();
//		System.out.println(value);
		if (value == null) {
			return false;
		}
		return true;
	}

}
