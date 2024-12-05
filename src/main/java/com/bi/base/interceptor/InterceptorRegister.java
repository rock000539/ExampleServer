/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.interceptor;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * Provides a simple interface for easy to register interceptor.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public interface InterceptorRegister {

	/**
	 * Add interceptor by register
	 *
	 * @param registry register
	 */
	void addInterceptors(InterceptorRegistry registry);

}
