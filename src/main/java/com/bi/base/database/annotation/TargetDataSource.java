/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides dynamic datasource.<br>
 * Switch DAO datasource to access different database.
 * Priority <code>Method -> Class</code>
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
	
	/**
	 * Datasource bean name.<br>
	 * The name of the bean must be config on initialize. 
	 * 
	 * @return datasource bean name
	 */
    String value() default "";
}
