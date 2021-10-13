package com.example.demo.domain.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.domain.test.model.Test;

public interface TestRepository extends JpaRepository<Test, Long>, JpaSpecificationExecutor<Test> {

}
