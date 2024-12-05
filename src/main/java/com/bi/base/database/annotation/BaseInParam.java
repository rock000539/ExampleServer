/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Provides base input parameter to define the actual stored procedure input parameter name.
 *
 * @author Allen Lin
 * @since 2.1.0
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface BaseInParam {

	/**
	 * stored procedure input parameter name.
	 *
	 * @return input parameter name
	 */
	String name() default "";

}
