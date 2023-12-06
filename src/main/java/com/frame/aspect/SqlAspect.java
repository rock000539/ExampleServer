/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.frame.aspect;

import com.frame.model.SqlTracer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * API SQL Trace Log設定
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@ConditionalOnProperty(name = "project.trace.sql.enabled", havingValue = "true")
@Slf4j
@Aspect
@Component
public class SqlAspect {

	@Autowired
	private List<SqlTracer> tracers;

	@Around("execution(* com.bi.base.database.jdbc.SqlUtilImpl.*(..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = joinPoint.proceed();
		addTracer(joinPoint.getArgs(), ((CodeSignature) joinPoint.getSignature()).getParameterNames(), result);
		return result;
	}

	private void addTracer(Object[] args, String[] argNames, Object result) {
		SqlTracer sqlTracer = new SqlTracer();

		for (int i = 0; i < args.length; i++) {
			if ("sql".equals(argNames[i])) {
				sqlTracer.setSql((String) args[i]);
			} else if ("paramMap".equals(argNames[i])) {
				sqlTracer.setParameter(args[i]);
			} else if ("batchValues".equals(argNames[i])) {
				sqlTracer.setParameter(args[i]);
			} else if ("args".equals(argNames[i])) {
				sqlTracer.setParameter(args[i]);
			} else if ("batchArgs".equals(argNames[i])) {
				sqlTracer.setParameter(args[i]);
			} else if ("params".equals(argNames[i])) {
				sqlTracer.setParameter(args[i]);
			}
		}

		sqlTracer.setData(result);

		if (StringUtils.isNotBlank(sqlTracer.getSql())
				&& RequestContextHolder.getRequestAttributes() != null) {
			tracers.add(sqlTracer);
		}
	}
}
