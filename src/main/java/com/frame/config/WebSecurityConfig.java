/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	public static final String LOGIN_PATH = "/login";

	public static final String LOGIN_FUNCTION_PATH = "/login/**";

	public static final String LOGIN_API_PATH = "/api/*/login/**";

	public static final String LOGOUT_PATH = "/logout";

	public static final String SSO_LOGIN_PATH = "/sso";

	public static final String DENIED_PATH = "/401";

	public static final String HOME_PATH = "/";

	public static final String[] ANONYMOUS_PATHS = {
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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("https://safe.com"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "PUT"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors()
				.and()
				.headers(headers -> headers
						.frameOptions(frame -> frame.sameOrigin()))
				.sessionManagement(session -> session
						.maximumSessions(1)
						.sessionRegistry(sessionRegistry())
						.expiredUrl("/?expired"))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN_PATH))
						.accessDeniedPage(LOGIN_PATH))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(ANONYMOUS_PATHS).permitAll()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage(LOGIN_PATH).permitAll())
				.logout(logout -> logout
						.logoutUrl(LOGOUT_PATH)
						.invalidateHttpSession(true)
						.logoutSuccessUrl("/logoutSuccess"))
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.ignoringRequestMatchers(LOGIN_API_PATH))
				.headers(headers -> headers.xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)).contentSecurityPolicy(cps -> cps.policyDirectives("script-src 'self' .....")));

		return http.build();
	}
}
