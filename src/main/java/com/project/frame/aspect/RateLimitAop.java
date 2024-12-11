/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.frame.aspect;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope
@Aspect
public class RateLimitAop {

	@Autowired
	private HttpServletResponse response;

	private RateLimiter rateLimiter = RateLimiter.create(5.0);

	@Pointcut(value = "@annotation(com.project.frame.annotation.RateLimitAspect)")
	public void serviceLimit() {

	}

	@Around("serviceLimit()")
	public Object around(ProceedingJoinPoint joinPoint) {
		Boolean flag = rateLimiter.tryAcquire();
		Object obj = null;
		try {
			if (flag) {
				obj = joinPoint.proceed();
			} else {
				String result = "系統繁忙中 請稍後再試";
				output(response, result);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("flag=" + flag + ",obj=" + obj);
		return obj;
	}

	public void output(HttpServletResponse response, String msg) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		ServletOutputStream outputStream = null;
		try {
			outputStream = response.getOutputStream();
			outputStream.write(msg.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}
}
