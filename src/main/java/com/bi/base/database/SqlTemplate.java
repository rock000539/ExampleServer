/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * The simple implementation of SQL template.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
public abstract class SqlTemplate implements TopSqlTemplate, PaginateSqlTemplate {

	protected final MessageFormat selectBaseFormat = new MessageFormat("SELECT {1} FROM {0} {2}");

	protected final MessageFormat selectWrapperFormat = new MessageFormat("SELECT T.* FROM ({0}) T");

	private final MessageFormat countFormat = new MessageFormat("SELECT COUNT(1) FROM {0} {1}");

	private final MessageFormat countWrapperFormat = new MessageFormat("SELECT COUNT(1) FROM ({0}) C");

	private final MessageFormat sortFormat = new MessageFormat("{0} ORDER BY {1}");

	private final MessageFormat insertFormat = new MessageFormat("INSERT INTO {0} ({1}) VALUES ({2})");

	private final MessageFormat updateFormat = new MessageFormat("UPDATE {0} SET {1} {2}");

	private final MessageFormat deleteFormat = new MessageFormat("DELETE FROM {0} {1}");

	/**
	 * Format simple query sql by information (SELECT {columns} FROM {table} {other}) {@link #selectBaseFormat}
	 * 
	 * @param table table name
	 * @param columns table columns
	 * @param other other SQL syntax
	 * @return formatted SQL
	 */
	public String formatSelectBaseSql(String table, String columns, String other) {
		return selectBaseFormat.format(new Object[]{table, columns, other});
	}

	/**
	 * Wrap query sql (SELECT T.* FROM ({sql}) T) {@link #selectWrapperFormat}
	 * 
	 * @param sql SQL query to execute
	 * @return formatted SQL
	 */
	public String formatSelectWrapperSql(String sql) {
		return selectWrapperFormat.format(new Object[]{sql});
	}

	/**
	 * Format count sql (SELECT COUNT(1) FROM {0} {1}) {@link #countFormat}
	 *
	 * @param table table name
	 * @param other other SQL syntax
	 * @return formatted SQL
	 */
	public String formatCountSql(String table, String other) {
		return countFormat.format(new Object[]{table, other});
	}

	/**
	 * Wrap count sql (SELECT COUNT(1) FROM ({sql}) C) {@link #countWrapperFormat}
	 * 
	 * @param sql SQL query to execute
	 * @return formatted SQL
	 */
	public String formatCountWrapperSql(String sql) {
		return countWrapperFormat.format(new Object[]{sql});
	}

	/**
	 * Wrap sort sql ({sql} ORDER BY {sort}) {@link #sortFormat}
	 * 
	 * @param sql SQL query to execute
	 * @param sort sort parameter information
	 * @return formatted SQL
	 */
	public String formatSortSql(String sql, Sort sort) {
		List<String> orders = new ArrayList<>();
		for (Order order : sort) {
			orders.add(order.getProperty().concat(" ").concat(order.getDirection().name()));
		}
		return sort.isSorted() ? sortFormat.format(new Object[]{sql, StringUtils.join(orders, ", ")}) : sql;
	}

	/**
	 * Format insert sql by information (INSERT INTO {table} ({columns}) VALUES ({values})) {@link #insertFormat}
	 * 
	 * @param table table name
	 * @param columns table columns
	 * @param values table column parameter variable
	 * @return formatted SQL
	 */
	public String formatInsertSql(String table, String columns, String values) {
		return insertFormat.format(new Object[]{table, columns, values});
	}

	/**
	 * Format update sql by information (UPDATE {table} SET {values} {other}) {@link #updateFormat}
	 * 
	 * @param table table name
	 * @param values table column parameter variable
	 * @param other other SQL syntax
	 * @return formatted SQL
	 */
	public String formatUpdateSql(String table, String values, String other) {
		return updateFormat.format(new Object[]{table, values, other});
	}

	/**
	 * Format delete sql by information (DELETE FROM {table} {other}) {@link #deleteFormat}
	 * 
	 * @param table table name
	 * @param other other SQL syntax
	 * @return formatted SQL
	 */
	public String formatDeleteSql(String table, String other) {
		return deleteFormat.format(new Object[]{table, other});
	}

}
