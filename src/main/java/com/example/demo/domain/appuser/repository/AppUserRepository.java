package com.example.demo.domain.appuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.appuser.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {

	@Query("SELECT au, r FROM AppUser au INNER JOIN Role r ON au.role = r.roleId WHERE au.userName = :userName")
	AppUser findByUserName(@Param("userName") String userName);
}
