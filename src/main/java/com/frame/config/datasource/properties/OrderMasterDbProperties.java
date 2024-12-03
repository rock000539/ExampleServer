/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.config.datasource.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Parker Huagn
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "orderMaster.datasource")
public class OrderMasterDbProperties {

	private String jdbcUrl;

	private String userName;

	private String passWord;

	private int maxLifetime;

	private int borrowConnectionTimeout;

	private int loginTimeout;

	private int maintenanceInterval;

	private int maxIdleTime;

	private String testQuery;

	private String uniqueResourceName;
}
