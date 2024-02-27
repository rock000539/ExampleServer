/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class XmlUtil {

	public static String objectToXml(Object obj) throws JsonProcessingException {
		return objectToXml(obj, false, null);
	}

	public static String objectToUtf8Xml(Object obj) throws JsonProcessingException {
		return objectToXml(obj, false, "UTF-8");
	}

	public static String objectToXml(Object obj, boolean noHeader, String encoding) throws JsonProcessingException {
		XmlMapper xmlMapper = new XmlMapper();
		String xml = xmlMapper.writeValueAsString(obj);

		if (encoding == null) {
			encoding = "BIG5";
		}

		if (!noHeader) {
			xml = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + xml;
		}
		log.debug("XmlMapper objectToXml: {}", xml);
		return xml;
	}

	public static <T> T xmlToObject(String xml, Class<T> parametrized, Class<?>... parameterClasses) throws IOException {
		XmlMapper xmlMapper = new XmlMapper();
		// 忽略沒有對應的XML欄位
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		xmlMapper.registerModule(new SimpleModule());
		T value = null;
		try {
			JavaType javaType = xmlMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
			value = xmlMapper.readValue(xml, javaType);
		} catch (Exception e) {
			log.error("XmlMapper error xmlToObjet: {}", xml);
			throw e;
		}
		log.debug("XmlMapper xmlToObjet: {}", xml);
		return value;
	}
}
