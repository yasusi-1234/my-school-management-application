package com.example.demo.controller.form;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.controller.form.FormEnum.DBInjectType;
import com.example.demo.controller.form.FormEnum.RegistrationData;
import com.example.demo.controller.form.validator.NoNull;
import com.example.demo.controller.form.validator.UploadFile;

import lombok.Data;

@Data
public class SchoolDataForm implements Serializable {

	private static final long serialVersionUID = 1L;
	@UploadFile
	private MultipartFile multipartFile;
	@NoNull
	private RegistrationData registationData;
	@NoNull
	private DBInjectType dbInjectType;

}
