/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.handler;

import com.bi.base.util.SpringUtil;
import com.bi.base.web.BaseApi;
import com.bi.base.web.model.ResultEntity;
import com.project.frame.annotation.TransParamCode;
import com.project.frame.annotation.TransParamField;
import com.project.frame.exception.BusinessException;
import com.project.frame.model.RestTracer;
import com.project.frame.model.SqlTracer;
import com.project.frame.model.enums.SourceType;
import com.project.frame.web.model.ApiResultEntity;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * API 資訊處裡
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Slf4j
@ControllerAdvice(assignableTypes = {BaseApi.class})
public class ApiResultHandler implements ResponseBodyAdvice<Object> {

	@Autowired
	private List<SqlTracer> sqlTracers;

	@Autowired
	private List<RestTracer> restTracers;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
		if (object instanceof ResultEntity) {
			doValueFilter(((ResultEntity) object).getData());
		} else {
			doValueFilter(object);
		}
		setTracer(object);
		return object;
	}

	private void setTracer(Object object) {
		try {
			if (object instanceof ApiResultEntity) {
				if (sqlTracers.size() > 0) {
					((ApiResultEntity) object).setSqlTracer(sqlTracers);
				}
				if (restTracers.size() > 0) {
					((ApiResultEntity) object).setRestTracer(restTracers);
				}
			}
		} catch (Exception e) {
			log.debug("Tracer error: ", e);
		}
	}

	private void doValueFilter(Object object) {
		// Handle page object
		if (object instanceof Page
				&& object != null
				&& !CollectionUtils.isEmpty(((Page<?>) object).getContent())) {
			((Page<?>) object).stream().parallel().forEach(obj -> transParamCode(obj));
			// Handle java collection object
		} else if (object instanceof Collection && !CollectionUtils.isEmpty((Collection<?>) object)) {
			((Collection<?>) object).parallelStream().forEach(obj -> transParamCode(obj));
			// Handle other object
		} else if (object != null) {
			transParamCode(object);
		}
	}

	private void transParamCode(Object object) {
		if (object == null) {
			return;
		}
		FieldUtils.getFieldsListWithAnnotation(object.getClass(), TransParamCode.class)
				.parallelStream()
				.forEach(field -> {
					TransParamCode annotation = field.getAnnotation(TransParamCode.class);
					field.setAccessible(true);
					try {
						String value = transParamCode(annotation, object, field.get(object));
						// Keep data default set value.
						if (value != null) {
							FieldUtils.writeField(field, object, value, true);
						}
					} catch (Exception e) {
						if (log.isDebugEnabled()) {
							log.debug("Translate parameter code error: {}", e);
						} else {
							log.warn("Translate parameter code error: {}", e.getMessage());
						}
					}
				});
		FieldUtils.getFieldsListWithAnnotation(object.getClass(), TransParamField.class)
				.parallelStream()
				.forEach(field -> {
					try {
						Object fieldObject = FieldUtils.readField(object, field.getName(), true);
						doValueFilter(fieldObject);
					} catch (IllegalAccessException | BeansException e) {
						log.warn("Translate parameter code error: ", e);
					}
				});
	}

	private String transParamCode(TransParamCode annotation, Object object, Object value) throws Exception {
		String result = null;
		String fieldName = annotation.fieldName();
		String key = annotation.key();
		SourceType sourceType = annotation.sourceType();
		Class<?> sourceValue = annotation.sourceValue();

		try {
			Object fieldObject = FieldUtils.readField(object, fieldName, true);
			String fieldValue = fieldObject != null ? String.valueOf(fieldObject) : null;

			// Translate parameter code by enums
			if (sourceType == SourceType.ENUMS
					&& sourceValue.isEnum()
					&& StringUtils.isNotEmpty(fieldName)) {
				result = Arrays.stream(sourceValue.getEnumConstants())
						.parallel()
						.filter(obj -> ((TransParamEnum) obj).equal(fieldValue))
						.map(obj -> ((TransParamEnum) obj).getMessage())
						.findFirst()
						.orElse(null);
				// Translate parameter code by system table
			} else if (sourceType == SourceType.SYS
					&& sourceValue.newInstance() instanceof TransParamService
					&& StringUtils.isNotEmpty(fieldName)) {
				TransParamService transParamService = SpringUtil.getBean(sourceValue);
				result = transParamService.getDescription(key, fieldValue, annotation, object, value);
			}
		} catch (IllegalAccessException | BeansException | InstantiationException e) {
			log.warn("Translate parameter code error: ", e);
		}
		return result;
	}

	@ExceptionHandler({BusinessException.class})
	public ResponseEntity<Object> businessException(BusinessException e) {
		ApiResultEntity resultEntity = new ApiResultEntity();
		BusinessException.BusinessReturnStatus businessReturnStatus = e.getReturnStatus();
		if (businessReturnStatus != null) {
			resultEntity.setReturnStatus(businessReturnStatus);
			resultEntity.setStatusMsg(businessReturnStatus.getReturnDesc());
		} else {
			resultEntity.setStatusMsg(e.getMessage());
		}
		setTracer(resultEntity);
		return new ResponseEntity<>(resultEntity, HttpStatus.OK);
	}

	@ExceptionHandler({AccessDeniedException.class})
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
		ResultEntity resultEntity = new ResultEntity();
		resultEntity.setStatus(HttpStatus.UNAUTHORIZED.value());
		resultEntity.setStatusMsg(e.getMessage());
		return new ResponseEntity<>(resultEntity, HttpStatus.UNAUTHORIZED);
	}
}
