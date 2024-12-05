/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.config;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.bi.base.database.transaction.AtomikosTransactionHolder;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.StringUtils;

/**
 * Provides multiple datasource transaction configuration.<br>
 * <code>Java Transaction API</code>
 * Use JTA will be generated transaction log file at runtime, must be careful
 * the file access when multiple program running.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnProperty(name = {"spring.datasource.enabled", "spring.jta.atomikos.enabled"}, havingValue = "true")
@Configuration("baseJtaTransactionConfig")
@EnableTransactionManagement
@EnableConfigurationProperties
public class JtaTransactionConfig {

	private UserTransaction userTransaction;

	private TransactionManager transactionManager;

	@Bean
	@ConfigurationProperties(prefix = "spring.jta.atomikos")
	public Properties atomikosProperties() {
		return new Properties();
	}

	@Bean(initMethod = "init", destroyMethod = "shutdownWait")
	public UserTransactionServiceImp userTransactionService(Properties atomikosProperties) {

		log.debug("Load default userTransactionService");

		Properties properties = new Properties();
		String transactionManagerId = atomikosProperties.getProperty("transaction-manager-id");
		if (StringUtils.hasText(transactionManagerId)) {
			properties.setProperty("com.atomikos.icatch.tm_unique_name", transactionManagerId);
		}

		String logBaseDir = atomikosProperties.getProperty("log-base-dir", "/default/log/dir"); // 替换为实际默认路径
		properties.setProperty("com.atomikos.icatch.log_base_dir", logBaseDir);

		properties.putAll(atomikosProperties);
		return new UserTransactionServiceImp(properties);
	}

	@Bean(destroyMethod = "close", initMethod = "init")
	public UserTransactionManager userTransactionManager(UserTransactionService userTransactionService) {

		log.debug("Load default userTransactionManager");

		UserTransactionManager manager = new UserTransactionManager();
		manager.setStartupTransactionService(false);
		manager.setForceShutdown(true);
		return manager;
	}

	@Bean
	@Primary
	public JtaTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager transactionManager) {

		log.debug("Load default transactionManager");

		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
		jtaTransactionManager.setAllowCustomIsolationLevels(true);

		AtomikosTransactionHolder.setTransaction(userTransaction);
		AtomikosTransactionHolder.setTransactionManager(transactionManager);

		return jtaTransactionManager;
	}
}
