/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.handler;

import com.bi.base.web.model.ResultEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Handle all web request exception.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Config error return attributes.
	 *
	 * @return error attributes
	 */
	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {
			@SuppressWarnings("unchecked")
			@Override
			public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
				Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
				ResultEntity<Object> resultEntity = new ResultEntity<>();
				resultEntity.setStatus((int) errorAttributes.get("status"));
				resultEntity.setStatusMsg((String) errorAttributes.get("error"));
				resultEntity.setStatusDesc((String) errorAttributes.get("message"));
				Map<String, Object> result = objectMapper.convertValue(resultEntity, Map.class);
				result.putAll(errorAttributes);
				return result;
			}

		};
	}
}
