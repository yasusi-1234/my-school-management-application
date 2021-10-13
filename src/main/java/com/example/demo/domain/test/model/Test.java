package com.example.demo.domain.test.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Test implements Serializable {

	private static final long serialVersionUID = -4692859943055760097L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long testId;
	@Column(nullable = false, length = 10)
	private String seasonName;
	@Column(nullable = false, length = 10)
	private String subjectName;
	@Column(nullable = false, length = 4)
	private int year;
	@Column(nullable = false, length = 1)
	private byte grade;
}
