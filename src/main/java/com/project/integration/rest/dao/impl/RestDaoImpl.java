/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.integration.rest.dao.impl;

import com.bi.base.i18n.util.I18nUtil;
import com.project.integration.rest.dao.RestDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author Parker Huang
 * @param <T>
 * @since 1.0.0
 */
@Slf4j
@Component
public class RestDaoImpl<T> implements RestDao {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public <T> T api(String url, Class<T> responseClazz) {
		return api(url, responseClazz, null, new HttpHeaders(), null);
	}

	@Override
	public <T> T api(String url, HttpMethod httpMethod, Class<T> responseClazz) {
		return api(url, responseClazz, httpMethod, null, null);
	}

	@Override
	public <T> T api(String url, HttpHeaders header, Class<T> responseClazz) {
		return api(url, responseClazz, null, header, null);
	}

	@Override
	public <T> T api(String url, Object req, Class<T> responseClazz) {
		return api(url, responseClazz, null, new HttpHeaders(), req);
	}

	@Override
	public <T> T api(String url, HttpMethod httpMethod, Object req, Class<T> responseClazz) {
		return api(url, responseClazz, httpMethod, null, req);
	}

	@Override
	public <T> T api(String url, HttpHeaders header, Object req, Class<T> responseClazz) {
		return api(url, responseClazz, null, header, req);
	}

	@Override
	public <T> T api(String url, @Nullable HttpMethod httpMethod, HttpHeaders header, Object req, Class<T> responseClazz) {
		return api(url, responseClazz, httpMethod, header, req);
	}

	@Override
	public <T> T api(String url, Type type, @Nullable HttpMethod httpMethod, @Nullable HttpHeaders header, @Nullable Object req) {
		return api(url, type, httpMethod, header, req, null);
	}

	@Override
	public <T> T api(String url, Type type, @Nullable HttpMethod httpMethod, @Nullable HttpHeaders header, @Nullable Object req, @Nullable RestTemplate restTemplate) {
		// Set default
		if (httpMethod == null)
			httpMethod = HttpMethod.POST;
		if (header == null) {
			header = new HttpHeaders();
		}
		if (CollectionUtils.isEmpty(header.getAccept())) {
			header.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		}
		if (header.getContentType() == null) {
			header.setContentType(MediaType.APPLICATION_JSON);
		}

		Timestamp startDt = new Timestamp(System.currentTimeMillis());
		ResponseEntity<T> responseEntity = null;
		T responseObject = null;
		String stackTrace = null;
		try {
			restTemplate = restTemplate != null ? restTemplate : this.restTemplate;
			responseEntity = restTemplate.exchange(url, httpMethod, new HttpEntity<>(req, header), ParameterizedTypeReference.forType(type));
			responseObject = responseEntity.getBody();
			return responseObject;
		} catch (Exception e) {
			stackTrace = ExceptionUtils.getStackTrace(e);
			throw new RuntimeException(I18nUtil.getMessage("rest.exception"), e);
		} finally {
			if (log.isTraceEnabled()) {
				log.trace("REST URL: {}, method: {}, header: {}, body: {}, response: {}", url, httpMethod, header, req, responseEntity);
			} else {
				log.debug("REST URL: {}, method: {}, header: {}", url, httpMethod, header);
			}
		}
	}
}
