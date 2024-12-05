/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.annotation.handler;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.bi.base.annotation.ApiVersion;
import com.bi.base.annotation.condition.ApiVersionCondition;

/**
 * Provides API version control.
 * That will combine an object contain all objects(extend, implement) to show latest version.
 * Priority <code>Method > Class > Parent Class</code>
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
public class ApiVersioningRequestMappingHandler extends RequestMappingHandlerMapping {

	/**
	 * The tag will be replaced to API current version.
	 */
	public static final String REQUEST_MAPPING_API_VARIABLE = "{apiVersion}";

	/**
	 * The API version prefix.
	 */
	private static final String API_VARIABLE_PREFIX = "v";

	@Override
	protected RequestCondition<ApiVersionCondition> getCustomTypeCondition(Class<?> handlerType) {
		ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
		return createCondition(apiVersion);
	}

	@Override
	protected RequestCondition<ApiVersionCondition> getCustomMethodCondition(Method method) {
		ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
		return createCondition(apiVersion);
	}

	private RequestCondition<ApiVersionCondition> createCondition(ApiVersion apiVersion) {
		return apiVersion == null ? null : new ApiVersionCondition(apiVersion.value());
	}

	/**
	 * Combine the object all API versions.
	 */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = this.createRequestMappingInfo(method, null);
        if (info != null) {
            ApiVersion apiVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
            apiVersion = apiVersion != null ? apiVersion : AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
            RequestMappingInfo typeInfo = this.createRequestMappingInfo(handlerType, apiVersion);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    /**
     * Set the latest API version number.
     *
     * @param element annotation element
     * @param apiVersion API version
     * @return mapping information
     */
	@SuppressWarnings("unchecked")
	private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element, ApiVersion apiVersion) {
		Set<RequestMapping> requestMappings = AnnotatedElementUtils.findAllMergedAnnotations(element, RequestMapping.class);
		RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class<?>) element) : this.getCustomMethodCondition((Method) element);
		RequestMappingInfo requestMappingInfo = null;

		for (RequestMapping requestMapping : requestMappings) {
			if (element instanceof Class && null != apiVersion) {
				try {
					// Dynamic modify RequestMapping annotation attribute
					InvocationHandler invocationHandler = Proxy.getInvocationHandler(requestMapping);
					Field field = invocationHandler.getClass().getDeclaredField("valueCache");
					// Open permission for SynthesizedAnnotationInvocationHandler valueCache variable
					field.setAccessible(true);
					Map<String, String[]> map = (Map<String, String[]>) field.get(invocationHandler);
					String[] paths = new String[requestMapping.path().length];
					for (int i = 0; i < requestMapping.path().length; i++) {
						paths[i] = replaceApiVariable(requestMapping.path()[i], String.valueOf(apiVersion.value()));
					}
					map.put("path", paths);
					String[] values = new String[requestMapping.value().length];
					for (int i = 0; i < requestMapping.value().length; i++) {
						values[i] = replaceApiVariable(requestMapping.value()[i], String.valueOf(apiVersion.value()));
					}
					map.put("value", values);
				} catch (Exception e) {
					log.error("Get API version error: ", e);
				}
			}

			if (requestMappingInfo == null) {
				requestMappingInfo = this.createRequestMappingInfo(requestMapping, condition);
			} else {
				requestMappingInfo = requestMappingInfo.combine(this.createRequestMappingInfo(requestMapping, condition));
			}
		}

		return requestMappingInfo;
	}

	/**
	 * Replace API version variable to the version number.
	 *
	 * @param source API path
	 * @param apiVersion API version number
	 * @return formatted API path
	 */
	public static String replaceApiVariable(String source, String apiVersion) {
		return source.replace(REQUEST_MAPPING_API_VARIABLE, API_VARIABLE_PREFIX.concat(apiVersion));
	}

}
