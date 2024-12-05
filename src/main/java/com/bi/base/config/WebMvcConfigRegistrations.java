/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.bi.base.annotation.handler.ApiVersioningRequestMappingHandler;

/**
 * The configuration to set the web basis.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Configuration("baseWebMvcConfigRegistrations")
public class WebMvcConfigRegistrations implements WebMvcRegistrations {

	/**
	 * Config API version handler.
	 *
	 * @return mapping handler
	 */
	@Override
	public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
		return new ApiVersioningRequestMappingHandler();
	}
}
