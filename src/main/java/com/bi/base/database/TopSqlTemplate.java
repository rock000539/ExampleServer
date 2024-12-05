/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database;

/**
 * The simple interface of SQL select top template.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public interface TopSqlTemplate {

	/**
	 * Format sql to fetch top row.
	 *
	 * @param sql SQL query to execute
	 * @param top fetch size
	 * @return formatted SQL
	 */
	String formatTopSql(String sql, int top);

}
