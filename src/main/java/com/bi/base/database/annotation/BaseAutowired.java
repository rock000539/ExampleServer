/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */

package com.bi.base.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Reference {@link org.springframework.beans.factory.annotation.Autowired},
 * dependency injection with {@link com.bi.base.database.service.BaseService} {@link com.bi.base.database.dao.BaseDao},
 * auto create association bean.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER})
@Documented
public @interface BaseAutowired {

    /**
     * Datasource name for ORM to access.
     *
     * @since 1.4.0
     * @return datasource name
     */
    String dataSourceName() default "";
}
