/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Repository;

import com.bi.base.database.config.DynamicDataSourceConfig;
import com.bi.base.database.datasource.DynamicDataSource;
import com.bi.base.database.model.enums.DatabaseType;

import javax.sql.DataSource;

/**
 * Provides the current target database mapping to SQL template.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@ConditionalOnBean(DynamicDataSourceConfig.class)
@NoArgsConstructor
@Repository
public class SqlTemplateProxy {

	@Autowired
	private DynamicDataSource dynamicDataSource;

	/**
	 * Initial.
	 *
	 * @since 1.4.0
	 * @param dynamicDataSource dynamic datasource that multiple datasource information
	 */
	public SqlTemplateProxy(DynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
	}

	public SqlTemplate getSqlTemplate() {
		return getSqlTemplate(dynamicDataSource.getDataSource());
	}

	/**
	 * Get SQL template.
	 *
	 * @since 1.4.0
	 * @param dataSource datasource
	 * @return SQL format template
	 */
	public static SqlTemplate getSqlTemplate(DataSource dataSource) {
		try {
			return DatabaseType.getSqlTemplate(dataSource);
		} catch (MetaDataAccessException | NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

}
