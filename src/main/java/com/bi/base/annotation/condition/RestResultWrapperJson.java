/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.annotation.condition;

import java.lang.reflect.Method;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

/**
 * Provides wrapper condition to wrapper JSON response data.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public class RestResultWrapperJson extends AbstractRestResultWrapperCondition {

	public RestResultWrapperJson(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, Object result, Method method, MediaType mediaType) {
		super(serverHttpRequest, serverHttpResponse, result, method, mediaType);
	}

	@Override
	public boolean condition() {
		return mediaType.toString().toUpperCase().contains("JSON");
	}

}
