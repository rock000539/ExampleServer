/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.util;

import java.util.*;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.lang.Nullable;

/**
 * Provides a simple utility to transform bean.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanUtil extends BeanUtils {

	/**
	 * Copies properties from one object to another.
	 *
	 * @param source copy source object
	 * @param destination copy destination object
	 */
	public static void copyNonNullProperties(Object source, Object destination) {
		BeanUtils.copyProperties(source, destination, getNullPropertyNames(source));
	}

	/**
	 * Returns an array of null properties of an object.
	 *
	 * @param source object
	 * @return null field
	 */
	private static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for (java.beans.PropertyDescriptor pd : pds) {
			// Check if value of this property is null then add it to the collection
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/**
	 * Transform bean to map object.
	 *
	 * @param bean transform source object
	 * @return map object
	 */
	public static Map<String, Object> beanToMap(@Nullable Object bean) {
		Map<String, Object> map = new HashMap<>();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet()) {
				map.put(key + "", beanMap.get(key));
			}
		}
		return map;
	}

	/**
	 * Transform map object to bean.
	 *
	 * @param map transform source map
	 * @param bean transform destination object
	 * @param <T> generic parameter for clazz
	 * @return mapped bean
	 */
	public static <T> T mapToBean(Map<String, Object> map, T bean) {
		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}

	/**
	 * Transform bean to map object.
	 *
	 * @param beans transform source object
	 * @param <T> generic parameter for clazz
	 * @return map object
	 */
	public static <T> List<Map<String, Object>> beansToMaps(@Nullable List<T> beans) {
		return beans != null ? beans.stream().map(BeanUtil::beanToMap).collect(Collectors.toList()) : new ArrayList<>();
	}

	/**
	 * Transform map object to bean.
	 *
	 * @param maps transform source map
	 * @param clazz transform destination class
	 * @param <T> generic parameter for clazz
	 * @return mapped bean
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> mapsToBeans(@Nullable List<Map<String, Object>> maps, Class<T> clazz) throws InstantiationException, IllegalAccessException {
		List<T> list = new ArrayList<>();
		if (maps != null && maps.size() > 0) {
			Map<String, Object> map;
			T bean;
			for (Map<String, Object> stringObjectMap : maps) {
				map = stringObjectMap;
				bean = clazz.newInstance();
				mapToBean(map, bean);
				list.add(bean);
			}
		}
		return list;
	}

}
