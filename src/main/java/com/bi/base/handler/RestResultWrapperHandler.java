/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.handler;

import com.bi.base.annotation.RestResultWrapper;
import com.bi.base.annotation.condition.AbstractRestResultWrapperCondition;
import com.bi.base.util.JsonParser;
import com.bi.base.web.model.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Handle all REST response.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class RestResultWrapperHandler implements ResponseBodyAdvice<Object> {

	@SuppressWarnings("rawtypes")
	@Autowired(required = false)
	private Class<? extends ResultEntity> resultEntity = ResultEntity.class;

	@Override
	public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
		Set<RestResultWrapper> resultWrapperMethod = AnnotatedElementUtils.findAllMergedAnnotations(methodParameter.getAnnotatedElement(), RestResultWrapper.class);
		Set<RestResultWrapper> resultWrapperClass = AnnotatedElementUtils.findAllMergedAnnotations(methodParameter.getContainingClass(), RestResultWrapper.class);
		return resultWrapperMethod.size() > 0 || resultWrapperClass.size() > 0;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> clazz, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
		Set<RestResultWrapper> resultWrapperMethod = AnnotatedElementUtils.findAllMergedAnnotations(methodParameter.getAnnotatedElement(), RestResultWrapper.class);
		Set<RestResultWrapper> resultWrapperClass = AnnotatedElementUtils.findAllMergedAnnotations(methodParameter.getContainingClass(), RestResultWrapper.class);
		Class[] paramsClass = new Class[]{ServerHttpRequest.class, ServerHttpResponse.class, Object.class, Method.class, MediaType.class};
		Object[] params = new Object[]{serverHttpRequest, serverHttpResponse, object, methodParameter.getMethod(), mediaType};

		// Use 'RestResultWrapper' annotation to decide response object will wrapper or not
		if (!(object instanceof ResultEntity) // Exclude object is 'ResultEntity'
				&& (object == null | !(object instanceof String) || !JsonParser.validJson((String) object, ResultEntity.class)) // Wrapper object is 'String'
				&& (resultWrapperMethod.size() != 0 || resultWrapperClass.size() != 0)
				&& (resultWrapperCondition(paramsClass, params, resultWrapperMethod) || resultWrapperCondition(paramsClass, params, resultWrapperClass))) {
			try {
				if (log.isDebugEnabled())
					log.debug("Wrapper object: {}", object);
				ResultEntity<Object> resultEntity = this.resultEntity.newInstance();
				if (object instanceof String && JsonParser.validJson((String) object)) {
					resultEntity.setData(JsonParser.entity((String) object, Map.class));
				} else {
					resultEntity.setData(object);
				}
				Method method = methodParameter.getMethod();
				Type type = method != null ? method.getGenericReturnType() : null;
				return String.class.equals(type) ? JsonParser.toJson(resultEntity) : resultEntity;
			} catch (Exception e) {
				log.warn("Can not wrapper object: ", e);
			}
		}
		return object;
	}

	/**
	 * Wrapper standard result data format by condition.
	 *
	 * @param paramsClass wrapper parameter class
	 * @param params wrapper parameter
	 * @param resultWrappers wrapper information
	 * @return need to wrapper result
	 */
	@SuppressWarnings("rawtypes")
	protected boolean resultWrapperCondition(Class[] paramsClass, Object[] params, Set<RestResultWrapper> resultWrappers) {
		boolean result = false;
		try {
			for (RestResultWrapper resultWrapper : resultWrappers) {
				for (Class<? extends AbstractRestResultWrapperCondition> clazz : resultWrapper.condition()) {
					@SuppressWarnings("rawtypes")
					Constructor constructor = clazz.getConstructor(paramsClass);
					AbstractRestResultWrapperCondition condition = (AbstractRestResultWrapperCondition) constructor.newInstance(params);
					if (!condition.condition())
						return false;
				}
			}
			if (resultWrappers.size() > 0)
				result = true;
		} catch (Exception e) {
			log.warn("Wrapper condition error: ", e);
		}
		return result;
	}

}
