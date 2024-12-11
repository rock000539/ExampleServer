/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.project.frame.aspect;

import com.project.frame.model.RestTracer;
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
 * API Trace Log設定
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@ConditionalOnProperty(name = "project.trace.rest.enabled", havingValue = "true")
@Slf4j
@Aspect
@Component
public class RestAspect {

	@Autowired
	private List<RestTracer> tracers;

	@Around("execution(* org.springframework.web.client.RestTemplate.*(..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = joinPoint.proceed();
		addTracer(joinPoint.getArgs(), ((CodeSignature) joinPoint.getSignature()).getParameterNames(), result);
		return result;
	}

	private void addTracer(Object[] args, String[] argNames, Object result) {
		RestTracer restTracer = new RestTracer();

		for (int i = 0; i < args.length; i++) {
			if ("url".equals(argNames[i])) {
				restTracer.setUrl((String) args[i]);
			} else if ("request".equals(argNames[i])) {
				restTracer.setRequest(args[i]);
			} else if ("requestEntity".equals(argNames[i])) {
				restTracer.setRequest(args[i]);
			}
		}

		restTracer.setResponse(result);

		if (StringUtils.isNotBlank(restTracer.getUrl())
				&& RequestContextHolder.getRequestAttributes() != null) {
			tracers.add(restTracer);
		}
	}
}
