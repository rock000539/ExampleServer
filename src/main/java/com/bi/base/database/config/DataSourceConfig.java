/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * The configuration support for JNDI and multiple datasource.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnExpression("'true'.equals('${spring.datasource.enabled}') && !'true'.equals('${spring.jta.atomikos.enabled}')")
@ConditionalOnMissingBean(name = "dataSource")
@Configuration("baseDataSourceConfig")
public class DataSourceConfig {

	@Value("${spring.datasource.jndiName:}")
	private String jndiName;

	@ConditionalOnProperty(name = "spring.datasource.jndiName")
	@Bean(value = "dataSource", destroyMethod = "")
    public DataSource jndiDataSource() {
		JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
		jndiDataSourceLookup.setResourceRef(true);

		log.debug("Load dataSource from JNDI: {}", jndiName);

		return jndiDataSourceLookup.getDataSource(jndiName);
    }

	@ConditionalOnMissingBean(name = "dataSource")
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		log.debug("Load default dataSource");
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}
}
