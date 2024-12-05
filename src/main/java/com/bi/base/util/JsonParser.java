/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides a simple JSON utility.<br>
 * Handle by <code>jackson</code> third party library.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Component
public class JsonParser {

	protected static ObjectMapper objectMapper;

	@Autowired
	private void set(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Maps json string to specified class.
	 *
	 * @param json string to parse
	 * @param clazz class of object in which json will be parsed
	 * @param <T> generic parameter for clazz
	 * @return mapped T class instance
	 * @throws IOException
	 */
	public static <T> T entity(String json, Class<T> clazz) throws IOException {
		return objectMapper.readValue(json, clazz);
	}

	/**
	 * Maps json string to {@link ArrayList} of specified class object instances.
	 *
	 * @param json string to parse
	 * @param clazz class of object in which json will be parsed
	 * @param <T> generic parameter for clazz
	 * @return mapped T class instance
	 * @throws IOException
	 */
	public static <T> ArrayList<T> arrayList(String json, Class<T> clazz) throws IOException {
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType type = typeFactory.constructCollectionType(ArrayList.class, clazz);
		return objectMapper.readValue(json, type);
	}

	/**
	 * Writes specified object as string.
	 *
	 * @param object object to write
	 * @return result json
	 * @throws IOException
	 */
	public static String toJson(Object object) throws IOException {
		return objectMapper.writeValueAsString(object);
	}

	/**
	 * Writes specified object as JsonNode.
	 *
	 * @param object object to write
	 * @return result JsonNode
	 */
	public static JsonNode toJsonNode(Object object) {
		return objectMapper.valueToTree(object);
	}

	/**
	 * Validate JSON from string.
	 *
	 * @param json json string
	 * @return result
	 */
	public static boolean validJson(String json) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.readTree(json);
			return !StringUtils.isNumeric(json) && !StringUtils.isBlank(json);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Validate JSON string can bind with object.
	 *
	 * @param json json string
	 * @return result
	 */
	public static <T> boolean validJson(String json, Class<T> clazz) {
		try {
			entity(json, clazz);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
