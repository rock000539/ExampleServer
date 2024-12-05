/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database;

import org.springframework.data.domain.Pageable;

/**
 * The simple interface of SQL pagination template.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public interface PaginateSqlTemplate {

	/**
	 * Format sql to pagination sql. (First page number of 0)
	 *
	 * @param sql SQL query to execution
	 * @param pageable pagination parameter information
	 * @return formatted SQL
	 */
    String formatPaginateSql(String sql, Pageable pageable);
}
