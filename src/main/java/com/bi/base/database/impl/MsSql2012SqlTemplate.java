/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.impl;

import com.bi.base.database.SqlTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a simple SQL template for <code>Microsoft SQL Server</code> database.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@ConditionalOnSingleCandidate(MsSql2012SqlTemplate.class)
@Component
public class MsSql2012SqlTemplate extends SqlTemplate {

	private final MessageFormat topFormat = new MessageFormat("SELECT TOP {1} * FROM ({0}) T");

	private final MessageFormat paginateFormat = new MessageFormat("{0} ORDER BY {3} OFFSET {1} ROWS FETCH NEXT {2} ROWS ONLY ");

	@Override
	public String formatTopSql(String sql, int top) {
		return topFormat.format(new Object[]{sql, top});
	}

	@Override
	public String formatPaginateSql(String sql, Pageable pageable) {
		Assert.isTrue(pageable.getSort().isSorted(), "MSSQL Pagination must be have sort arguments");
		int startRow = pageable.getPageNumber() * pageable.getPageSize();
		List<String> orders = new ArrayList<>();

		for (Order order : pageable.getSort()) {
			orders.add(order.getProperty().concat(" ").concat(order.getDirection().name()));
		}
		sql = formatSelectWrapperSql(sql);
		return paginateFormat.format(new Object[]{sql, String.valueOf(startRow), String.valueOf(pageable.getPageSize()), StringUtils.join(orders, ", ")});
	}

}
