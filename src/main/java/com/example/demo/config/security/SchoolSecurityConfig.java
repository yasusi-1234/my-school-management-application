package com.example.demo.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SchoolSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// セキュリティ設定を無視するリクエスト設定
		// 静的リソースに対するアクセスはセキュリティ設定を無視する
		web.ignoring().antMatchers("/css/**", "/img/**", "/js/**");
		// swagger
		web.ignoring().antMatchers("/webjars/**", "/swagger-resources/**", "/v2/api-docs**", "/static**");

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()// アクセス権限の設定
				// アクセス制限の無いURL
				.antMatchers("/home**", "/home/**", "/school/login", "/rest/**").permitAll()
				// swagger
				.antMatchers("/swagger-ui.html**").permitAll()
				// その他は認証済みである事
				.anyRequest().authenticated();

		http.formLogin()
				// ログイン画面
				.loginPage("/school/login")
				// 認証処理
//				.loginProcessingUrl("/authenticate")
				.loginProcessingUrl("/school/login")
				// ユーザーのパラメータ名
				.usernameParameter("userId")
				// passwordのパラメータ名
				.passwordParameter("password")
				// ログイン成功 決まった場所への遷移
				.defaultSuccessUrl("/school/main", true)
				// ログイン失敗
				.failureUrl("/school/login?error=true");


		http.logout()
				// ログアウト処理
				.logoutRequestMatcher(new AntPathRequestMatcher("/school/logout"))
				// ログアウト成功時の遷移先
				.logoutSuccessUrl("/school/login")
				// ログアウト時に削除するクッキー名
				.deleteCookies("JSESSIONID")
				// ログアウト時のセッション破棄を有効化
				.invalidateHttpSession(true).permitAll();

//				.and().sessionManagement()
		// セッションが無効な時の遷移先
//				.invalidSessionUrl("/invalidSession");

		// 一時的にCSRF設定無効
//		http.csrf().disable();
//		http.cors().configurationSource(corsConfiguration());
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder builder,
			@Qualifier("appUserDetailsService") UserDetailsService userDetailsService, PasswordEncoder encoder)
			throws Exception {
		// ログイン処理時にユーザー情報を、DBから取得
		builder.eraseCredentials(true).userDetailsService(userDetailsService).passwordEncoder(encoder);
	}

	private CorsConfigurationSource corsConfiguration() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
		corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Access-Control-Allow-Headers",
				"Access-Control-Allow-Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers",
				"Cache-Control", "Content-Type", "Accept-Language"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

}
