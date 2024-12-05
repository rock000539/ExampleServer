/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.impl;

import java.text.MessageFormat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bi.base.database.SqlTemplate;

/**
 * Provides a simple SQL template for <code>Oracle</code> database.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@ConditionalOnSingleCandidate(OracleSqlTemplate.class)
@Component
public class OracleSqlTemplate extends SqlTemplate {
	
	private final MessageFormat topFormat = new MessageFormat("{0} FETCH FIRST {1} ROWS ONLY");
	
	private final MessageFormat paginateFormat = new MessageFormat("SELECT * FROM ( SELECT P.*, ROWNUM AS RNUM FROM ({0}) P ) WHERE RNUM > {1} AND RNUM <= {2}");

	@Override
	public String formatTopSql(String sql, int top) {
		return topFormat.format(new Object[] {sql, top});
	}
	
	@Override
	public String formatPaginateSql(String sql, Pageable pageable) {
		int startRow = pageable.getPageNumber() * pageable.getPageSize();
		int endRow = (pageable.getPageNumber() + 1) * pageable.getPageSize();
		sql = formatSelectWrapperSql(sql);
		sql = formatSortSql(sql, pageable.getSort());
		return paginateFormat.format(new Object[] {sql, String.valueOf(startRow), String.valueOf(endRow)});
	}

}
