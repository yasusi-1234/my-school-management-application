package com.example.demo.controller.form.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@Constraint(validatedBy = UploadFileValidator.class)
public @interface UploadFile {

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String message() default "ファイルが選ばれていないかサポートされていないファイル形式です";

	@Documented
	@Retention(RUNTIME)
	@Target({ FIELD, METHOD })
	@interface List {
		UploadFile[] value();
	}
}
