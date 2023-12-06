/*
 * Copyright (c) 2022 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.exampleServer.rest.dao;

import java.lang.reflect.Type;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
public interface RestDao {

	<T> T api(String url, Class<T> responseClazz);

	<T> T api(String url, HttpMethod httpMethod, Class<T> responseClazz);

	<T> T api(String url, HttpHeaders header, Class<T> responseClazz);

	<T> T api(String url, Object req, Class<T> responseClazz);

	<T> T api(String url, HttpMethod httpMethod, Object req, Class<T> responseClazz);

	<T> T api(String url, HttpHeaders header, Object req, Class<T> responseClazz);

	<T> T api(String url, @Nullable HttpMethod httpMethod, HttpHeaders header, Object req, Class<T> responseClazz);

	<T> T api(String url, Type type, @Nullable HttpMethod httpMethod, @Nullable HttpHeaders header, @Nullable Object req);

	<T> T api(String url, Type type, @Nullable HttpMethod httpMethod, HttpHeaders header, @Nullable Object req, @Nullable RestTemplate restTemplate);
}
