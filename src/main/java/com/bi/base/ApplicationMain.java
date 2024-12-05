/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Provide application launch.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@ComponentScan(basePackages = "${project.componentScan}")
@EnableAutoConfiguration
public class ApplicationMain extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationMain.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ApplicationMain.class);
	}
}
