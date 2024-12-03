/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.handler;

import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseChar;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

public class CsvUploadHandler<E> {

	private Class<E> entityClazz;

	public CsvUploadHandler(@Nullable Class<E> entityClazz) {
		this.entityClazz = entityClazz;
	}

	public List<E> readWithCsvMapReader(InputStream fio, List<String> customHeader, String encoding) throws Exception {
		Class<E> clazz = this.entityClazz;
		List<E> result = new ArrayList<E>();
		encoding = encoding == null ? "Utf8" : encoding;
		try (InputStreamReader isReader = new InputStreamReader(fio, encoding);
				Reader reader = new BufferedReader(isReader);
				ICsvMapReader mapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE);) {

			// the header columns are used as the keys to the Map
			String[] header = mapReader.getHeader(true);
			fitCustomHeader(header, customHeader);
			CellProcessor[] processors = getProcessors(customHeader, clazz);
			Map<String, String> fieldMapping = getFieldMapping(header, customHeader);

			Map<String, Object> customerMap;
			while ((customerMap = mapReader.read(header, processors)) != null) {
				Constructor<E> constructor = clazz.getConstructor();
				E obj = constructor.newInstance();
				for (String customFieldName : customerMap.keySet()) {
					String fieldName = fieldMapping.get(customFieldName);

					if (StringUtils.isNotEmpty(fieldName)) {
						Field field = null;
						try {
							field = clazz.getDeclaredField(fieldName);
							field.setAccessible(true);

							Object value = customerMap.get(customFieldName);
							field.set(obj, value);
						} catch (NoSuchFieldException e) {
							continue;
						}
					}
				}
				result.add(obj);
			}
		}
		return result;
	}

	private void fitCustomHeader(String[] header, List<String> customHeader) {
		if (header.length != customHeader.size()) {

			if (customHeader.size() > header.length) {
				while (customHeader.size() > header.length) {
					customHeader.remove(customHeader.size() - 1);
				}
			}

			if (header.length > customHeader.size()) {
				for (int i = customHeader.size(); i < header.length; i++) {
					customHeader.add("Empty Header");
				}
			}
		}
	}

	private Map<String, String> getFieldMapping(String[] header, List<String> customHeader) {

		if (header.length != customHeader.size()) {
			if (customHeader.size() > header.length) {
				List<String> newCustomHeader = new ArrayList<String>();
				for (int i = 0; i < header.length; i++) {
					newCustomHeader.add(customHeader.get(i));
				}
				customHeader = newCustomHeader;
			}

			if (header.length > customHeader.size()) {
				List<String> newHeaders = new ArrayList<String>();
				for (int i = 0; i < customHeader.size(); i++) {
					newHeaders.add(header[i]);
				}
				header = new String[customHeader.size()];
				header = newHeaders.toArray(header);
			}
		}

		Map<String, String> fieldMapping = new HashMap<String, String>();
		if (ArrayUtils.isEmpty(header)) {
			return fieldMapping;
		}

		for (int i = 0; i < header.length; i++) {
			String key = header[i];
			String value = customHeader.get(i);
			fieldMapping.put(key, value);
		}
		return fieldMapping;
	}

	private CellProcessor[] getProcessors(List<String> customHeader, Class<E> clazz) throws NoSuchFieldException, SecurityException {
		String[] properties = new String[customHeader.size()];
		properties = customHeader.toArray(properties);

		CellProcessor[] processors = new CellProcessor[properties.length];
		initialise(properties, processors, clazz);
		return processors;
	}

	public void initialise(String[] properties, CellProcessor[] processors, Class<E> clazz) throws NoSuchFieldException, SecurityException {
		for (int i = 0; i < properties.length; i++) {
			String fieldName = properties[i];
			Field field = null;

			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				processors[i] = new Optional();
				continue;
			}

			boolean isNotNull = field.isAnnotationPresent(NotNull.class);
			Class<?> type = field.getType();

			if (type == double.class || type == Double.class) {
				if (isNotNull) {
					processors[i] = new Optional(new ParseDouble());
				} else {
					processors[i] = new ParseDouble();
				}
			} else if (type == int.class || type == Integer.class) {
				if (isNotNull) {
					processors[i] = new ParseInt();
				} else {
					processors[i] = new Optional(new ParseInt());
				}
			} else if (type == float.class || type == Float.class) {
				if (isNotNull) {
					processors[i] = new ParseDouble();
				} else {
					processors[i] = new Optional(new ParseDouble());
				}
			} else if (type == long.class || type == Long.class) {
				if (isNotNull) {
					processors[i] = new ParseLong();
				} else {
					processors[i] = new Optional(new ParseLong());
				}
			} else if (type == short.class || type == Short.class) {
				if (isNotNull) {
					processors[i] = new ParseInt();
				} else {
					processors[i] = new Optional(new ParseInt());
				}
			} else if (type == String.class) {
				processors[i] = new Optional();
			} else if (type == char.class || type == Character.class) {
				if (isNotNull) {
					processors[i] = new ParseChar();
				} else {
					processors[i] = new Optional(new ParseChar());
				}
			} else if (type == boolean.class || type == Boolean.class) {
				if (isNotNull) {
					processors[i] = new ParseChar();
				} else {
					processors[i] = new Optional(new ParseChar());
				}
			} else if (type == Date.class) {
				if (isNotNull) {
					processors[i] = new ParseDate("yyyy/MM/dd");
				} else {
					processors[i] = new Optional(new ParseDate("yyyy/MM/dd"));
				}
			}
		}
	}
}
