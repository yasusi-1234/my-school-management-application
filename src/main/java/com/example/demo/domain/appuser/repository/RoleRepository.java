package com.example.demo.domain.appuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.appuser.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	Role findByRoleName(String roleName);
}
