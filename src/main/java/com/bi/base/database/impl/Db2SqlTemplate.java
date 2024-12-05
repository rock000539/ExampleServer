/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.bi.base.database.SqlTemplate;
import org.springframework.util.Assert;

/**
 * Provides a simple SQL template for <code>IBM DB2</code> database.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@ConditionalOnSingleCandidate(Db2SqlTemplate.class)
@Component
public class Db2SqlTemplate extends SqlTemplate {

	private final MessageFormat topFormat = new MessageFormat("{0} FETCH FIRST {1} ROWS ONLY");

	private final MessageFormat paginateFormat = new MessageFormat("SELECT * FROM ( SELECT ROW_NUMBER() OVER(ORDER BY {3}) AS RNUM, * FROM ({0}) S ) P WHERE P.RNUM BETWEEN {1} AND {2}");

	@Override
	public String formatTopSql(String sql, int top) {
		return topFormat.format(new Object[]{sql, top});
	}

	@Override
	public String formatPaginateSql(String sql, Pageable pageable) {
		Assert.isTrue(pageable.getSort().isSorted(), "DB2 Pagination must be have sort arguments");
		int startRow = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
		int endRow = (pageable.getPageNumber() + 1) * pageable.getPageSize();
		List<String> orders = new ArrayList<>();

		for (Order order : pageable.getSort()) {
			orders.add(order.getProperty().concat(" ").concat(order.getDirection().name()));
		}
		sql = formatSelectWrapperSql(sql);
		return paginateFormat.format(new Object[]{sql, String.valueOf(startRow), String.valueOf(endRow), StringUtils.join(orders, ", ")});
	}

}
