/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.service.impl;

import com.bi.base.database.dao.BaseSpDao;
import com.bi.base.database.service.BaseSpService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This implementation provides a simple stored procedure that quickly access by object.
 *
 * @author Allen Lin
 * @since 2.1.0
 */
@NoArgsConstructor
public class BaseSpServiceImpl<M extends BaseSpDao<T>, T> implements BaseSpService<T> {

	@Autowired
	protected M baseSpDao;

	/**
	 * For {@link com.bi.base.database.annotation.BaseAutowired} that inject spring bean manually.
	 *
	 * @param baseSpDao data access object
	 */
	public BaseSpServiceImpl(M baseSpDao) {
		this.baseSpDao = baseSpDao;
	}

	@Override
	public T execute() {
		return baseSpDao.execute();
	}

	@Override
	public T execute(T entity) {
		return baseSpDao.execute(entity);
	}
}
