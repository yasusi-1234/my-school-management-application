package com.example.demo.config.security.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.domain.appuser.model.AppUser;

import lombok.Data;

@Data
public class AppUserDetails implements UserDetails {

	private static final long serialVersionUID = 7223959681190149468L;

	public AppUserDetails(AppUser appUser) {
		this.appUser = appUser;
		this.password = appUser.getPassword();
		this.userName = appUser.getUserName();
		this.authorities = AuthorityUtils.createAuthorityList(appUser.getRole().getRoleName());
	}

	private AppUser appUser;

	private String password;

	private String userName;

	private List<GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
