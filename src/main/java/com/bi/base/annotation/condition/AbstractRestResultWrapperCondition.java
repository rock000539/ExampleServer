/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.annotation.condition;

import java.lang.reflect.Method;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provides interface for implementing REST wrapper condition.
 * That will be decide response wrapper or not.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractRestResultWrapperCondition {

	/**
	 * Server HTTP request information.
	 */
	protected ServerHttpRequest serverHttpRequest;

	/**
	 * Server HTTP response information.
	 */
	protected ServerHttpResponse serverHttpResponse;

	/**
	 * REST API result data.
	 */
	protected Object result;

	/**
	 * REST API definition method.
	 */
	protected Method method;

	/**
	 * REST API definition media type.
	 */
	protected MediaType mediaType;

	/**
	 * Condition to wrapper API result.
	 *
	 * @return need wrapper
	 */
	public abstract boolean condition();
}
