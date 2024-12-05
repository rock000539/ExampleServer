/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.config;

import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Provides multiple datasource configuration.<br>
 * Use Non XA datasource on default, if want to more support that need
 * change to XA datasource, but database need to config for XA.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnProperty(name = { "spring.datasource.enabled", "spring.jta.atomikos.enabled" }, havingValue = "true")
@ConditionalOnMissingBean(name = "dataSource")
@Configuration("baseJtaDataSourceConfig")
@EnableConfigurationProperties
public class JtaDataSourceConfig {

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource jtaDataSource() {
		log.debug("Load default JTA non-XA dataSource");
		return DataSourceBuilder.create().type(AtomikosNonXADataSourceBean.class).build();
	}
}
