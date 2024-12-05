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
 * Provides a simple SQL template for <code>Firebird</code> database.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@ConditionalOnSingleCandidate(FirebirdSqlTemplate.class)
@Component
public class FirebirdSqlTemplate extends SqlTemplate {

	private final MessageFormat topFormat = new MessageFormat("SELECT FIRST {1} LIMIT {0}");

	@Override
	public String formatTopSql(String sql, int top) {
		return topFormat.format(new Object[] {sql, top});
	}

	@Deprecated
	@Override
	public String formatPaginateSql(String sql, Pageable pageable) {
		throw new UnsupportedOperationException("Unsupported format pagination in firebird db");
	}

}
