package com.example.demo.config.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.demo.domain.appuser.model.AppUser;
import com.example.demo.domain.appuser.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!StringUtils.hasText(username)) {
			throw new UsernameNotFoundException("リクエストされたユーザー名のユーザーは存在しません");
//			throw new IllegalArgumentException("username is empty.");
		}

		AppUser appUser = repository.findByUserName(username);

		if (appUser == null) {
			throw new UsernameNotFoundException("リクエストされたユーザー名のユーザーは存在しません");
		}
		return new AppUserDetails(appUser);
	}

}
