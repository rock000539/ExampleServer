/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.config;

import com.bi.base.interceptor.InterceptorRegister;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * The configuration to set the web basis.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Configuration("baseWebMvcConfig")
public class WebMvcConfig implements WebMvcConfigurer, ApplicationContextAware {

	@Getter
	private static ApplicationContext applicationContext;

	@Autowired(required = false)
	private List<InterceptorRegister> interceptorRegisters = new ArrayList<>();

	/**
	 * Unify timezone that JAVA timezone and jackson library. <br>
	 * This configuration set 'spring.jackson.timeZone' property on jackson library.
	 *
	 * @since 1.4.0
	 * @return mapping builder
	 */
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return builder -> builder.timeZone(TimeZone.getDefault());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Set default page mapping.
	 *
	 * @param registry register
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/index.html");
	}

	/**
	 * Set all interceptors.
	 *
	 * @param registry register
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		interceptorRegisters.forEach(interceptorRegisterService -> interceptorRegisterService.addInterceptors(registry));
	}

}
