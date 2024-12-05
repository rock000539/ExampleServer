/*
 * Copyright (c) 2022 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.dao;

/**
 * The interface provides simple stored procedure by object.
 *
 * @author Allen Lin
 * @since 2.1.0
 */
public interface BaseSpDao<T> {

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
