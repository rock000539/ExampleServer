/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.config.datasource;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.frame.config.datasource.properties.OrderSlaveDbProperties;
import com.mysql.cj.jdbc.MysqlXADataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * @author Parker Huagn
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = {"spring.datasource.enabled", "spring.jta.atomikos.enabled"}, havingValue = "true")
public class OrderSlaveDataSourceConfig {

	@Bean("orderSlaveDataSource")
	DataSource dataSource(OrderSlaveDbProperties orderSlaveDbProps) throws SQLException {
		MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
		mysqlXADataSource.setUrl(orderSlaveDbProps.getJdbcUrl());
		mysqlXADataSource.setPassword(orderSlaveDbProps.getPassWord());
		mysqlXADataSource.setUser(orderSlaveDbProps.getUserName());
		mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);

		AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		xaDataSource.setXaDataSource(mysqlXADataSource);
		xaDataSource.setUniqueResourceName(orderSlaveDbProps.getUniqueResourceName());
		xaDataSource.setBorrowConnectionTimeout(orderSlaveDbProps.getBorrowConnectionTimeout());
		xaDataSource.setLoginTimeout(orderSlaveDbProps.getLoginTimeout());
		xaDataSource.setMaintenanceInterval(orderSlaveDbProps.getMaintenanceInterval());
		xaDataSource.setMaxIdleTime(orderSlaveDbProps.getMaxIdleTime());
		xaDataSource.setTestQuery(orderSlaveDbProps.getTestQuery());
		xaDataSource.setUniqueResourceName(orderSlaveDbProps.getUniqueResourceName());
		return xaDataSource;
	}

	@Bean(name = "orderSlaveSqlSessionFactory")
	SqlSessionFactory sqlSessionFactory(@Qualifier("orderSlaveDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		return bean.getObject();
	}

	@Bean(name = "orderSlaveDataSourceTransactionManager")
	DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("orderSlaveDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "orderSlaveSqlSessionTemplate")
	SqlSessionTemplate sqlSessionTemplate(@Qualifier("orderSlaveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
