package com.example.demo.domain.grade.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.demo.domain.appuser.model.AppUser;

import lombok.Data;

@Entity
@Data
public class UserGradeClass implements Serializable {

	private static final long serialVersionUID = 1522764244277621656L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userGradeId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private AppUser appUser;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "gradeId")
	private GradeClass gradeClass;
}
