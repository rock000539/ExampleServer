/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.frame.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring security 設定
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	public static final String LOGIN_PATH = "/login";

	public static final String LOGIN_FUNCTION_PATH = "/login/**";

	public static final String LOGIN_API_PATH = "/api/*/login/**";

	public static final String LOGOUT_PATH = "/logout";

	public static final String SSO_LOGIN_PATH = "/sso";

	public static final String DENIED_PATH = "/401";

	public static final String HOME_PATH = "/";

	public static final String[] ANONYMOUS_PATHS = new String[]{
			LOGIN_PATH, SSO_LOGIN_PATH, LOGOUT_PATH, DENIED_PATH, LOGIN_FUNCTION_PATH, LOGIN_API_PATH
	};

	@Value("${security.domain.enabled:false}")
	private boolean domainEnabled;

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("https://safe.com"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "PUT"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		return source;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		List<String> anonymousPath = new ArrayList<>();
		anonymousPath.addAll(Arrays.stream(ANONYMOUS_PATHS).collect(Collectors.toList()));

		http.cors()
				.and()
				.headers()
				.frameOptions()
				.sameOrigin()
				.and()
				.sessionManagement()
				.maximumSessions(1)
				.sessionRegistry(sessionRegistry())
				.expiredUrl("/?expired")
				.and()
				.sessionFixation()
				.migrateSession()
				.and()
				.exceptionHandling()
				.and()
				.authorizeRequests()
				.antMatchers(anonymousPath.stream().toArray(String[]::new))
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.formLogin()
				.loginPage(LOGIN_PATH)
				.permitAll()
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN_PATH))
				.accessDeniedPage(LOGIN_PATH)
				.and()
				.logout()
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.logoutSuccessUrl("/logoutSuccess")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.and()
				.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.ignoringAntMatchers("/api/*/login/**")
				.and()
				.headers()
				.referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
				.and()
				.addHeaderWriter(new StaticHeadersWriter("Permissions-Policy", "geolocation=(self)"));

		if (domainEnabled) {
			http.headers()
					.contentSecurityPolicy("upgrade-insecure-requests;")
					.and()
					.httpStrictTransportSecurity()
					.maxAgeInSeconds(31536000)
					.includeSubDomains(true)
					.preload(true);
		}
	}

	@Override
	public void configure(WebSecurity webSecurity) {
		// AuthenticationTokenFilter will ignore the below paths
		webSecurity
				.ignoring()
				// Allow anonymous resource requests
				.antMatchers("/logoutSuccess/**", "/login/**", "/images/**", "/img/**", "/model/**", "/css/**", "/js/**", "/fonts/**", "/plugins/**", "/favicon.ico");
	}
}
