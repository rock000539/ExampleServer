/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.frame.config.datasource;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * @author Parker Huagn
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = {"spring.datasource.enabled", "spring.jta.atomikos.enabled"}, havingValue = "true")
@EnableTransactionManagement
public class AtomikosTransactionConfig {

	@Bean(name = "userTransaction")
	public UserTransaction userTransaction() throws Throwable {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(10000);
		return (UserTransaction) userTransactionImp;
	}

	@Bean(name = "atomikosTransactionManager")
	public TransactionManager atomikosTransactionManager() throws Throwable {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(false);
		return (TransactionManager) userTransactionManager;
	}

	@Bean(name = "jtaTransactionManager")
	@DependsOn({"userTransaction", "atomikosTransactionManager"})
	public PlatformTransactionManager transactionManager() throws Throwable {
		UserTransaction userTransaction = userTransaction();
		TransactionManager atomikosTransactionManager = atomikosTransactionManager();
		return new JtaTransactionManager(userTransaction, atomikosTransactionManager);
	}
}
