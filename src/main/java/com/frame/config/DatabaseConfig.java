/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Parker Huagn
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class DatabaseConfig {

	@Value("${jasypt.encryptor.password}")
	private String JASYPT_ENCRYPTOR_PWD;

	@Value("${spring.datasource.username}")
	private String userName;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.jdbcUrl}")
	private String url;

	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;

	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(JASYPT_ENCRYPTOR_PWD);
		config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);
		return encryptor;
	}

	@Bean
	@Primary
	public DataSource primaryDataSource() {
		return getDataSource();
	}

	private DataSource getDataSource() {
		try {
			String password = this.password;
			if (PropertyValueEncryptionUtils.isEncryptedValue(this.password)) {
				password = PropertyValueEncryptionUtils.decrypt(this.password, stringEncryptor());
			}

			return DataSourceBuilder.create()
					.type(HikariDataSource.class)
					.username(userName)
					.password(password)
					.url(url)
					.driverClassName(driverClassName)
					.build();
		} catch (Exception e) {
			log.error("DataSource config error: ", e);
			throw new RuntimeException("DataSource config error: ", e);
		}
	}
}
