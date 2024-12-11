/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.frame.config;

import com.project.frame.converter.JpaDtoConvert;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

@Configuration
public class JpaConverterConfig {

	@PostConstruct
	public void init() {
		GenericConversionService genericConversionService = ((GenericConversionService) DefaultConversionService.getSharedInstance());
		genericConversionService.addConverter(new JpaDtoConvert());
	}
}
