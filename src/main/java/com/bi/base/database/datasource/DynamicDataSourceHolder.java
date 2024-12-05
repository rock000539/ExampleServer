/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.datasource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Keep all datasource keys.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicDataSourceHolder {

	private static final ThreadLocal<String> dataSourceThreadLocal = new ThreadLocal<>();

	private static final Set<String> dataSourceKeys = new HashSet<>();

	/**
	 * Set current thread datasource key.
	 *
	 * @param dataSourceKey datasource key
	 */
	public static void setDataSourceKey(String dataSourceKey) {
		dataSourceThreadLocal.set(dataSourceKey);
	}

	/**
	 * Get current thread datasource key.
	 *
	 * @return datasource key
	 */
	public static String getDataSourceKey() {
		return dataSourceThreadLocal.get();
	}

	/**
	 * Clear current thread datasource key.
	 */
	public static void clearDataSourceKey() {
		dataSourceThreadLocal.remove();
	}

	/**
	 * Pool of datasource key contain datasource key.
	 *
	 * @param dataSourceKey datasource key
	 * @return contain key
	 */
	public static boolean containsDataSourceKey(String dataSourceKey) {
		return dataSourceKeys.contains(dataSourceKey);
	}

	/**
	 * Add datasource key to pool.
	 *
	 * @param dataSourceKey datasource key
	 * @return is successful
	 */
	public static boolean addDataSourceKey(String dataSourceKey) {
		return dataSourceKeys.add(dataSourceKey);
	}
}
