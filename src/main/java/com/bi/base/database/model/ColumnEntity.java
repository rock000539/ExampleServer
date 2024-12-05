/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.model;

import lombok.Data;

import java.io.Serializable;


/**
 * Provides column of entity information.
 *
 * @author Allen Lin
 * @since 1.4.0
 */
@Data
public class ColumnEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field java type.
	 */
	private Class<?> type;

	/**
	 * Field name.
	 */
	private String fieldName;

	/**
	 * Mapping to table column name.
	 */
	private String columnName;

	/**
	 * Is ID.
	 */
	private boolean isId;

	/**
	 * Is auto increment.
	 */
	private boolean isAutoIncrement;

}
