/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.annotation;

import com.google.common.base.CaseFormat;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The stored procedure entity in database.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface BaseSp {

	/**
	 * (Optional) The name of the stored procedure.
	 * Defaults to the entity name.
	 *
	 * @return stored procedure name
	 */
	String name() default "";

	/**
	 * (Optional) The catalog of the table.
	 * Defaults to the default catalog.
	 *
	 * @since 2.1.0
	 * @return catalog
	 */
	String catalog() default "";

	/**
	 * (Optional) The schema of the table.
	 * Defaults to the default schema for user.
	 *
	 * @since 2.1.0
	 * @return schema
	 */
	String schema() default "";

	/**
	 * Transfer class's name format to an actual stored procedure name.<br>
	 * This is current using class naming format.
	 *
	 * @return format from
	 */
	CaseFormat clazzFormat() default CaseFormat.UPPER_CAMEL;

	/**
	 * Transfer class's name format to an actual stored procedure name.<br>
	 * This is actual stored procedure naming format.
	 *
	 * @return format to
	 */
	CaseFormat storedProcedureFormat() default CaseFormat.UPPER_UNDERSCORE;

}
