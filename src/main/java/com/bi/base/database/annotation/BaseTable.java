/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.common.base.CaseFormat;

/**
 * The table entity in database.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface BaseTable {

	/**
	 * (Optional) The name of the table.
	 * Defaults to the entity name.
	 *
	 * @return table name
	 */
	String name() default "";

	/**
	 * (Optional) The catalog of the table.
	 * Defaults to the default catalog.
	 *
	 * @return catalog
	 */
	String catalog() default "";

	/**
	 * (Optional) The schema of the table.
	 * Defaults to the default schema for user.
	 *
	 * @return schema
	 */
	String schema() default "";

	/**
	 * Transfer class's name format to an actual table name.<br>
	 * This is current using class naming format.
	 *
	 * @return format from
	 */
	CaseFormat clazzFormat() default CaseFormat.UPPER_CAMEL;

	/**
	 * Transfer class's name format to an actual table name.<br>
	 * This is actual table naming format.
	 *
	 * @return format to
	 */
	CaseFormat tableFormat() default CaseFormat.UPPER_UNDERSCORE;

	/**
	 * Transfer field's name format to an actual table column's name.<br>
	 * This is current using field naming format.
	 *
	 * @return format from
	 */
	CaseFormat fieldFormat() default CaseFormat.LOWER_CAMEL;

	/**
	 * Transfer field's name format to an actual table column's name.<br>
	 * This is actual table column's naming format.
	 *
	 * @return format to
	 */
	CaseFormat columnFormat() default CaseFormat.UPPER_UNDERSCORE;

}
