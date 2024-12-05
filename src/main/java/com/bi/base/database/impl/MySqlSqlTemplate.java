/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.impl;

import java.text.MessageFormat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bi.base.database.SqlTemplate;

/**
 * Provides a simple SQL template for <code>MySql</code> database.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@ConditionalOnSingleCandidate(MySqlSqlTemplate.class)
@Component
public class MySqlSqlTemplate extends SqlTemplate {

	private final MessageFormat topFormat = new MessageFormat("{0} LIMIT {1}");

	@Override
	public String formatTopSql(String sql, int top) {
		return topFormat.format(new Object[]{sql, top});
	}

	@Override
	public String formatPaginateSql(String sql, Pageable pageable) {
		int startRow = pageable.getPageNumber() * pageable.getPageSize();
		int size = pageable.getPageSize();
		String page = String.valueOf(startRow).concat(", ").concat(String.valueOf(size));
		sql = formatSelectWrapperSql(sql);
		sql = formatSortSql(sql, pageable.getSort());
		return topFormat.format(new Object[]{sql, page});
	}

}
