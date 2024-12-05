/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.datasource;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementing the dynamic data source. All the database access by this
 * to decide the target database.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

	/**
	 * Get all datasource count.
	 *
	 * @since 1.3.0
	 */
	@Getter
	@Setter
	private int size;

	/**
	 * Hold all spring's {@link org.springframework.jdbc.core.JdbcTemplate}.
	 */
	@Setter
	private Map<Object, JdbcTemplate> targetJdbcTemplate;

	/**
	 * Hold all spring's {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}.
	 */
	@Setter
	private Map<Object, NamedParameterJdbcTemplate> targetNamedParameterJdbcTemplate;

	@Getter
	@Setter
	private JdbcTemplate defaultJdbcTemplate;

	@Getter
	@Setter
	private NamedParameterJdbcTemplate defaultNamedParameterJdbcTemplate;

	/**
	 * Current datasource key.
	 *
	 * @return datasource key
	 */
    @Override
    protected Object determineCurrentLookupKey() {
    	String dataSourceKey = DynamicDataSourceHolder.getDataSourceKey();
    	log.debug("Get dynamic dataSource key: {}", dataSourceKey);
    	return dataSourceKey;
    }

    /**
     * Current datasource.
     *
     * @return current thread datasource
     */
    public DataSource getDataSource() {
		return determineTargetDataSource();
	}

    /**
     * Current datasource with spring's {@link org.springframework.jdbc.core.JdbcTemplate}.
     *
     * @return current thread {@link org.springframework.jdbc.core.JdbcTemplate}
     */
    public JdbcTemplate getJdbcTemplate() {
		return determineCurrentLookupKey() != null ? targetJdbcTemplate.get(determineCurrentLookupKey()) : defaultJdbcTemplate;
	}

    /**
     * Current datasource with spring's {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}.
     *
     * @return current thread {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}
     */
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return determineCurrentLookupKey() != null ? targetNamedParameterJdbcTemplate.get(determineCurrentLookupKey()) : defaultNamedParameterJdbcTemplate;
    }

}
