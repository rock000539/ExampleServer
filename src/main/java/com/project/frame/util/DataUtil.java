/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.project.frame.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataUtil {

	// data Object must has @NoArgsConstructor annotation.
	public static <T> T summary(List<T> dataList, Class<T> clazz) {
		try {
			T summaryData = clazz.getDeclaredConstructor().newInstance();

			for (Method getter : clazz.getMethods()) {
				if (isGetter(getter)) {
					String propertyName = propertyNameFromGetter(getter);

					BigDecimal sum = dataList.stream()
							.filter(data -> data != null)
							.map(data -> {
								try {
									BigDecimal value = (BigDecimal) getter.invoke(data);
									return value != null ? value : BigDecimal.ZERO;
								} catch (IllegalAccessException | InvocationTargetException e) {
									log.error("Exception:{}", e.getMessage());
									return BigDecimal.ZERO;
								}
							})
							.reduce(BigDecimal.ZERO, BigDecimal::add);

					propertyName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
					Method setter = clazz.getMethod("set" + propertyName, getter.getReturnType());
					setter.invoke(summaryData, sum);
				}
			}

			return summaryData;
		} catch (InstantiationException
				| IllegalAccessException
				| NoSuchMethodException
				| InvocationTargetException e) {
			log.error("Exception:{}", e.getMessage());
			return null;
		}
	}

	private static boolean isGetter(Method method) {
		return method.getName().startsWith("get")
				&& method.getParameterCount() == 0
				&& method.getReturnType() == BigDecimal.class;
	}

	private static String propertyNameFromGetter(Method getter) {
		String methodName = getter.getName();
		return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
	}
}
