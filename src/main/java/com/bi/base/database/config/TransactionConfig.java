/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides single datasource transaction configuration.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnExpression("'true'.equals('${spring.datasource.enabled}') && !'true'.equals('${spring.jta.atomikos.enabled}')")
@Configuration("baseTransactionConfig")
@EnableTransactionManagement
public class TransactionConfig {

	@ConditionalOnMissingBean({ PlatformTransactionManager.class })
	@Bean
	public DataSourceTransactionManager transaction(DataSource dataSource) {
		log.debug("Load default transaction");
		return new DataSourceTransactionManager(dataSource);
	}
}
