/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.service;

/**
 * The interface provides a simple stored procedure.
 *
 * @author Allen Lin
 * @since 2.1.0
 */
public interface BaseSpService<T> {

	/**
	 * Execute stored procedure.
	 *
	 * @return stored procedure entity
	 */
	T execute();

	/**
	 * Execute stored procedure
	 *
	 * @param entity stored procedure entity
	 * @return stored procedure entity
	 */
	T execute(T entity);

}
