/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.Mapping;

import com.bi.base.annotation.condition.AbstractRestResultWrapperCondition;

/**
 * Provides the condition to decide unify format API response data or not.<br>
 * All conditions must be true, otherwise not be established.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RestResultWrapper {

	/**
	 * Get REST API result wrapper condition class.
	 *
	 * @return result wrapper condition class
	 */
	@AliasFor("condition")
	Class<? extends AbstractRestResultWrapperCondition>[] value() default {};
	
	/**
	 * Get REST API result wrapper condition class.
	 *
	 * @return result wrapper condition class
	 */
	@AliasFor("value")
	Class<? extends AbstractRestResultWrapperCondition>[] condition() default {};
}
