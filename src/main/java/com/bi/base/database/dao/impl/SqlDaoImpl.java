/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.bi.base.database.SqlTemplateProxy;
import com.bi.base.database.SqlUtil;

/**
 * Provides a simple implementation of the SQL access database object.<br>
 * All DAO must be extend this object.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public abstract class SqlDaoImpl {

	@Autowired
	protected SqlUtil exeSql;

	@Autowired
	protected SqlTemplateProxy sqlTemplateProxy;
}
