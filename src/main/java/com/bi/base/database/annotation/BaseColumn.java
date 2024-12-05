/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Provides base column to define the actual table column's name,
 * that will be used in SQL of access database.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface BaseColumn {

	/**
	 * Table column's name.
	 *
	 * @return column name
	 */
	String name() default "";

}
