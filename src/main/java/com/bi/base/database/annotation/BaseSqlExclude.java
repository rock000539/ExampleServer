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
 * The field will be ignored in the SQL.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BaseSqlExclude {}
