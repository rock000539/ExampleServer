/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.annotation;

import com.bi.base.database.service.Generator;
import com.bi.base.database.service.impl.AutoIncrementGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Provides generator to generate column value,
 * that will be used in SQL of access database.
 *
 * @author Allen Lin
 * @since 1.1.0
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface BaseGeneratorValue {

	/**
	 * The class by primary key generator.
	 *
	 * @return primary key generator
	 */
	Class<? extends Generator> value() default AutoIncrementGenerator.class;

}
