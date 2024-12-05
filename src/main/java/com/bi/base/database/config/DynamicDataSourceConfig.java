/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.config;

import com.bi.base.database.datasource.DynamicDataSource;
import com.bi.base.database.datasource.DynamicDataSourceHolder;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Provides dynamic datasource configuration.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnProperty(name = "spring.datasource.enabled", havingValue = "true")
@Configuration("baseDynamicDataSourceConfig")
@EnableConfigurationProperties(JdbcProperties.class)
public class DynamicDataSourceConfig {

	public static final String DYNAMIC_DATA_SOURCE = "dynamicDataSource";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private JdbcProperties jdbcProperties;

	@Bean(name = DYNAMIC_DATA_SOURCE)
	public DynamicDataSource dynamicDataSource() {
		Map<Object, Object> targetDataSources = new HashMap<>();
		Map<Object, JdbcTemplate> targetJdbcTemplate = new HashMap<>();
		Map<Object, NamedParameterJdbcTemplate> targetNamedParameterJdbcTemplate = new HashMap<>();
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		JdbcTemplate jdbcTemplate;

		String[] dataSourceNames = appContext.getBeanNamesForType(DataSource.class);
		int dataSourceSize = 0;
		for (String name : dataSourceNames) {
			if (!DYNAMIC_DATA_SOURCE.equals(name)) {
				DataSource ds = appContext.getBean(name, DataSource.class);
				jdbcTemplate = getJdbcTemplate(ds);
				targetDataSources.put(name, ds);
				targetJdbcTemplate.put(name, jdbcTemplate);
				targetNamedParameterJdbcTemplate.put(name, getNamedParameterJdbcTemplate(jdbcTemplate));
				DynamicDataSourceHolder.addDataSourceKey(name);
				dataSourceSize++;
				log.debug("Dynamic add datasource: {}", name);
			}
		}

		log.debug("Dynamic targetDataSources: {}", targetDataSources);

		jdbcTemplate = getJdbcTemplate(dataSource);

		dynamicDataSource.setSize(dataSourceSize);
		dynamicDataSource.setTargetDataSources(targetDataSources);
		dynamicDataSource.setTargetJdbcTemplate(targetJdbcTemplate);
		dynamicDataSource.setTargetNamedParameterJdbcTemplate(targetNamedParameterJdbcTemplate);
		dynamicDataSource.setDefaultTargetDataSource(dataSource);
		dynamicDataSource.setDefaultJdbcTemplate(jdbcTemplate);
		dynamicDataSource.setDefaultNamedParameterJdbcTemplate(getNamedParameterJdbcTemplate(jdbcTemplate));
		dynamicDataSource.afterPropertiesSet();
		return dynamicDataSource;
	}

	protected JdbcTemplate getJdbcTemplate(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JdbcProperties.Template template = jdbcProperties.getTemplate();
		jdbcTemplate.setFetchSize(template.getFetchSize());
		jdbcTemplate.setMaxRows(template.getMaxRows());
		if (template.getQueryTimeout() != null) {
			jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
		}
		return jdbcTemplate;
	}

	protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
		return new NamedParameterJdbcTemplate(jdbcTemplate);
	}

}
