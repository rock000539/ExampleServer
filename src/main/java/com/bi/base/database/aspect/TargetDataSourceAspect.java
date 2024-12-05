/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.aspect;

import com.bi.base.database.annotation.TargetDataSource;
import com.bi.base.database.config.DynamicDataSourceConfig;
import com.bi.base.database.datasource.DynamicDataSource;
import com.bi.base.database.datasource.DynamicDataSourceHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Switch current correspond datasource.<br>
 * Handle by <code>TargetDataSource</code>
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@ConditionalOnBean(DynamicDataSourceConfig.class)
@Slf4j
@Aspect
@Order(-10)
@Component
public class TargetDataSourceAspect {

	@Autowired
	private DynamicDataSource dynamicDataSource;

	/**
	 * TargetDataSource priority <code>Method -> Class</code>
	 * Contains all objects.(extend, implement)
	 *
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.bi.base.database.dao.impl.SqlDaoImpl+.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
		try {
			if (dynamicDataSource.getSize() > 1) {
				MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
				Method targetMethod = methodSignature.getMethod();
				TargetDataSource method = AnnotatedElementUtils.findMergedAnnotation(targetMethod, TargetDataSource.class);

				if (method != null) {
					String targetDataSource = method.value();
					if (DynamicDataSourceHolder.containsDataSourceKey(targetDataSource)) {
						DynamicDataSourceHolder.setDataSourceKey(targetDataSource);
						log.debug("Dynamic datasource set: {}", targetDataSource);
					}
				} else {
					Class<?> targetClazz = pjp.getTarget().getClass();
					TargetDataSource clazz = AnnotatedElementUtils.findMergedAnnotation(targetClazz, TargetDataSource.class);
					if (clazz != null) {
						String targetDataSource = clazz.value();
						if (DynamicDataSourceHolder.containsDataSourceKey(targetDataSource)) {
							DynamicDataSourceHolder.setDataSourceKey(targetDataSource);
							log.debug("Dynamic datasource set: {}", targetDataSource);
						}
					}
				}
			}
			return pjp.proceed();
		} finally {
			DynamicDataSourceHolder.clearDataSourceKey();
		}
    }
}
