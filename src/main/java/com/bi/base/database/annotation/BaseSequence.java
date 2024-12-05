/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The sequence in database.
 * 
 * @author Allen Lin
 * @since 1.2.0
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface BaseSequence {

	/**
	 * Sequence name.
	 *
	 * @return sequence name
	 */
	String name();

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

}
