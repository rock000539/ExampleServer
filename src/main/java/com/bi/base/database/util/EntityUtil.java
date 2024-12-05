/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.util;

import com.bi.base.database.annotation.*;
import com.bi.base.database.model.ColumnEntity;
import com.bi.base.database.service.impl.AutoIncrementGenerator;
import com.google.common.base.CaseFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides a simple utility for base table entity.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class EntityUtil {

	/**
	 * Get stored procedure name.
	 *
	 * @param clazz stored procedure entity class
	 * @return stored procedure name
	 */
	public static String getStoredProcedureName(Class<?> clazz) {
		BaseSp baseSp = getBaseSp(clazz);
		String name = null;

		if (baseSp != null) {
			name = baseSp.name();
			if (StringUtils.isBlank(name))
				name = baseSp.clazzFormat().to(baseSp.storedProcedureFormat(), clazz.getSimpleName());
		}

		return name;
	}

	/**
	 * Get table name.
	 *
	 * @param clazz table entity class
	 * @return table name
	 */
	public static String getTableName(Class<?> clazz) {
		BaseTable baseTable = getBaseTable(clazz);
		String name = null;

		if (baseTable != null) {
			String catalog = baseTable.catalog();
			String schema = baseTable.schema();
			name = baseTable.name();
			if (StringUtils.isBlank(name))
				name = baseTable.clazzFormat().to(baseTable.tableFormat(), clazz.getSimpleName());
			if (StringUtils.isNotBlank(schema))
				name = schema.concat(".").concat(name);
			if (StringUtils.isNotBlank(catalog))
				name = catalog.concat(".").concat(name);
		}

		return name;
	}

	/**
	 * Get ID name for table.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @param clazz table entity class
	 * @param alias format column string with alias (SQL syntax)
	 * @return table ID name
	 */
	public static Set<String> getIdsName(Class<?> clazz, boolean alias) {
		BaseTable baseTable = getBaseTable(clazz);

		Assert.isTrue(baseTable != null, "It is not a table entity");

		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, BaseId.class);
		return fields.stream()
				.filter(field -> !isIgnoreColumn(field))
				.map(field -> getColumnName(baseTable, field, alias))
				.collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Get ID value for table.
	 * Map by field name.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @param entity table entity
	 * @return table ID parameter
	 */
	public static Map<String, SqlParameterValue> getIdsValue(Object entity) {
		return getColumnsValue(FieldUtils.getFieldsListWithAnnotation(entity.getClass(), BaseId.class), entity, null);
	}

	/**
	 * Get all columns name without BaseSqlExclude annotation for table.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @param clazz table entity class
	 * @param alias format column string with alias (SQL syntax)
	 * @return table column name
	 */
	public static Set<String> getColumnsName(Class<?> clazz, boolean alias) {
		BaseTable baseTable = getBaseTable(clazz);

		Assert.isTrue(baseTable != null, "It is not a table entity");

		List<Field> fields = FieldUtils.getAllFieldsList(clazz);
		return fields.stream()
				.filter(field -> !isIgnoreColumn(field))
				.map(field -> getColumnName(baseTable, field, alias))
				.collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Get all columns value without BaseSqlExclude annotation for table.
	 * Map by field name.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @param entity table entity
	 * @param excludeFieldName exclude field name
	 * @return table column parameter
	 */
	public static Map<String, SqlParameterValue> getColumnsValue(Object entity, @Nullable Set<String> excludeFieldName) {
		return getColumnsValue(FieldUtils.getAllFieldsList(entity.getClass()), entity, excludeFieldName);
	}

	private static Map<String, SqlParameterValue> getColumnsValue(List<Field> fields, Object obj, @Nullable Set<String> excludeFieldName) {
		BaseTable baseTable = getBaseTable(obj.getClass());

		Assert.isTrue(baseTable != null, "It is not a table entity");

		return fields.stream()
				.filter(field -> !isIgnoreColumn(field) && (excludeFieldName == null || !excludeFieldName.contains(field.getName())))
				.collect(HashMap::new, (map, field) -> {
					try {
						String fieldName = field.getName();
						String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
						Method method = MethodUtils.getMatchingAccessibleMethod(obj.getClass(), methodName);
						if (method != null) {
							Object value = MethodUtils.invokeMethod(obj, methodName);
							map.put(fieldName, transParamValue(field, value));
						} else {
							// For lombok boolean type field.
							methodName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
							method = MethodUtils.getMatchingAccessibleMethod(obj.getClass(), methodName);
							if (method != null) {
								Object value = MethodUtils.invokeMethod(obj, methodName);
								map.put(fieldName, transParamValue(field, value));
							}
						}
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						log.error("Entity access error: ", e);
						throw new RuntimeException(e);
					}
				}, HashMap::putAll);
	}

	/**
	 * Enhance DAO efficacy with SQL.<br>
	 * For {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}
	 *
	 * @param field field in table entity
	 * @param value parameter value
	 * @return parameter value
	 */
	private static SqlParameterValue transParamValue(Field field, Object value) {
		int type = StatementCreatorUtils.javaTypeToSqlParameterType(field.getType());
		return new SqlParameterValue(type, value);
	}

	/**
	 * Set columns value without BaseSqlExclude annotation.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @param entity table entity
	 * @param values mapping to table entity's value
	 * @return table entity
	 */
	public static <T> T setFieldsValue(T entity, Map<String, ?> values) {
		List<Field> fields = FieldUtils.getAllFieldsList(entity.getClass());
		Map<String, String> fieldWithSetter = new HashMap<>();

		fields.forEach(field -> {
			if (!isIgnoreColumn(field)) {
				String fieldName = field.getName();
				String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
				Method method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName, field.getType());
				if (method != null)
					fieldWithSetter.put(fieldName, methodName);
			}
		});

		values.forEach((key, value) -> {
			String methodName = fieldWithSetter.get(key);
			try {
				if (methodName != null) {
					if (value instanceof SqlParameterValue) {
						MethodUtils.invokeMethod(entity, methodName, ((SqlParameterValue) value).getValue());
					} else {
						MethodUtils.invokeMethod(entity, methodName, value);
					}
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				log.error("Entity access error: ", e);
				throw new RuntimeException(e);
			}
		});

		return entity;
	}

	/**
	 * Get all ignore handle columns.<br>
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @param clazz table entity class
	 * @return field name
	 */
	public static List<String> ignoreColumns(Class<?> clazz) {
		return Stream.of(FieldUtils.getAllFields(clazz)).filter(EntityUtil::isIgnoreColumn)
				.map(Field::getName).collect(Collectors.toList());
	}

	/**
	 * Check columns is ignore.<br>
	 * Ignore by {@link BaseSqlExclude}, final field
	 *
	 * @since 1.4.0
	 * @param field field in table entity
	 * @return is ignore
	 */
	public static boolean isIgnoreColumn(Field field) {
		// Ignore BaseSqlExclude, serialVersionUID and final field.
		return field.isAnnotationPresent(BaseSqlExclude.class) || Modifier.isFinal(field.getModifiers());
	}

	/**
	 * Convert entity to sql parameter without BaseSqlExclude annotation.
	 * Value {@link #getColumnsValue}
	 *
	 * @param entity table entity
	 * @return table all parameter
	 */
	public static Map<String, SqlParameterValue> entityToMapParams(Object entity) {
		return getColumnsValue(entity, null);
	}

	/**
	 * Convert entity to sql parameter without BaseSqlExclude annotation.
	 * Value {@link #getColumnsValue}
	 *
	 * @param entities table entity
	 * @return table all parameter
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> Map<String, SqlParameterValue>[] entityToMapsParams(T... entities) {
		return Arrays.stream(entities).map(EntityUtil::entityToMapParams).toArray(Map[]::new);
	}

	/**
	 * Get column that match field name or column name by name.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @param clazz table entity class
	 * @param name field name or table column name
	 * @param alias alias format column string with alias (SQL syntax)
	 * @return table column name
	 */
	public static String getColumnName(Class<?> clazz, String name, boolean alias) {
		BaseTable baseTable = getBaseTable(clazz);
		String result = null;

		Assert.isTrue(baseTable != null, "It is not a table entity");

		List<Field> fields = FieldUtils.getAllFieldsList(clazz);
		for (Field field : fields) {
			String fieldName = field.getName();
			if (!isIgnoreColumn(field)) {
				String columnName = getColumnName(baseTable, field, alias);
				if (fieldName.equals(name) || columnName.equals(name)) {
					result = columnName;
				}
			}
		}

		return result;
	}

	/**
	 * Column is table auto increment.
	 *
	 * @since 1.1.0
	 * @param clazz table entity class
	 * @param name field name or column name.
	 * @return is auto increment
	 */
	public static boolean isAutoIncrement(Class<?> clazz, String name) {
		BaseTable baseTable = getBaseTable(clazz);

		Assert.isTrue(baseTable != null, "It is not a table entity");

		List<Field> fields = FieldUtils.getAllFieldsList(clazz);
		return fields.stream()
				.filter(field -> field.getName().equals(name) || getColumnName(baseTable, field, false).equals(name))
				.anyMatch(EntityUtil::isAutoIncrement);
	}

	/**
	 * Field is table auto increment.
	 *
	 * @since 1.4.0
	 * @param field field in table entity
	 * @return is auto increment
	 */
	public static boolean isAutoIncrement(Field field) {
		return field.isAnnotationPresent(BaseGeneratorValue.class) && (AutoIncrementGenerator.class.equals(field.getAnnotation(BaseGeneratorValue.class).value()));
	}

	/**
	 * Get name of auto increment in table.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @since 1.1.0
	 * @param clazz table entity class
	 * @param isTable decide return value (true: table column ,false: method field)
	 * @return auto increment column name
	 */
	public static String getAutoIncrementName(Class<?> clazz, boolean isTable) {
		List<Field> fields = FieldUtils.getAllFieldsList(clazz);
		BaseTable baseTable = getBaseTable(clazz);
		Set<String> columnName = fields.stream()
				.filter(field -> !isIgnoreColumn(field) // Exclude field.
						&& field.isAnnotationPresent(BaseGeneratorValue.class)
						&& AutoIncrementGenerator.class.equals(field.getAnnotation(BaseGeneratorValue.class).value())) // Is auto increment.
				.map(field -> isTable ? getColumnName(baseTable, field, false) : field.getName()) // Get name.
				.collect(Collectors.toSet());

		Assert.isTrue(columnName.size() <= 1, "Column of auto increment must be only");

		return columnName.stream().findFirst().orElse(null);
	}

	/**
	 * Set field value of auto increment.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @since 1.1.0
	 * @param entity table entity
	 * @param value auto increment value
	 * @return table entity
	 */
	public static <T> T setAutoIncrementFieldValue(T entity, Number value) {
		List<Field> fields = FieldUtils.getAllFieldsList(entity.getClass());

		fields = fields.stream().filter(field -> !isIgnoreColumn(field)
				&& field.isAnnotationPresent(BaseGeneratorValue.class)
				&& (AutoIncrementGenerator.class.equals(field.getAnnotation(BaseGeneratorValue.class).value()))).collect(Collectors.toList());

		Assert.isTrue(fields.size() <= 1, "Column of auto increment must be only");
		Assert.isTrue(fields.size() > 0, "Entity doesn't have auto increment field");

		Field field = fields.get(0);

		if (field != null) {
			String fieldName = field.getName();
			String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			Class<?> fieldClazz = field.getType();
			Method method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName, fieldClazz);
			try {
				if (method != null) {
					Object retrieveValue;
					if (fieldClazz.equals(Number.class)) {
						retrieveValue = value;
					} else if (fieldClazz.equals(int.class) || fieldClazz.equals(Integer.class)) {
						retrieveValue = value.intValue();
					} else if (fieldClazz.equals(short.class) || fieldClazz.equals(Short.class)) {
						retrieveValue = value.shortValue();
					} else if (fieldClazz.equals(long.class) || fieldClazz.equals(Long.class)) {
						retrieveValue = value.longValue();
					} else if (fieldClazz.equals(float.class) || fieldClazz.equals(Float.class)) {
						retrieveValue = value.floatValue();
					} else if (fieldClazz.equals(double.class) || fieldClazz.equals(Double.class)) {
						retrieveValue = value.doubleValue();
					} else if (fieldClazz.equals(byte.class) || fieldClazz.equals(Byte.class)) {
						retrieveValue = value.byteValue();
					} else if (fieldClazz.equals(String.class)) {
						retrieveValue = value.toString();
					} else {
						throw new UnsupportedOperationException("No match field class with value");
					}
					MethodUtils.invokeMethod(entity, methodName, retrieveValue);
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | UnsupportedOperationException e) {
				log.error("Entity access error: ", e);
				throw new RuntimeException(e);
			}
		}
		return entity;
	}

	/**
	 * Set field value of generator.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @since 1.2.0
	 * @param entity table entity
	 * @return table entity
	 */
	public static <T> T setGeneratorFieldValue(T entity) {
		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(entity.getClass(), BaseGeneratorValue.class);

		fields.stream()
				.filter(field -> !isIgnoreColumn(field))
				.forEach(field -> {
					String fieldName = field.getName();
					String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
					Class<?> fieldClazz = field.getType();
					Method method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName, fieldClazz);
					try {
						if (method != null) {
							Object value = field.getAnnotation(BaseGeneratorValue.class).value().newInstance().getValue(entity, field);
							MethodUtils.invokeMethod(entity, methodName, value);
						}
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | UnsupportedOperationException | InstantiationException e) {
						log.error("Entity access error: ", e);
						throw new RuntimeException(e);
					}
				});

		return entity;
	}

	/**
	 * Get actually parameter value.<br>
	 * For {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}
	 *
	 * @param value parameter
	 * @return format to {@link org.springframework.jdbc.core.SqlParameterValue}
	 */
	public static Object getParamValue(Object value) {
		if (value != null) {
			if (value instanceof SqlParameterValue) {
				SqlParameterValue sqlParameterValue = (SqlParameterValue) value;
				return sqlParameterValue.getValue();
			} else {
				return value;
			}
		} else {
			return null;
		}
	}

	/**
	 * Get actually parameter value.<br>
	 * For {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}
	 *
	 * @since 1.4.0
	 * @param values parameter
	 * @return format to {@link org.springframework.jdbc.core.SqlParameterValue}
	 */
	public static Object[] getParamValue(Object... values) {
		return Arrays.stream(values).map(value -> {
			if (value instanceof SqlParameterValue) {
				SqlParameterValue sqlParameterValue = (SqlParameterValue) value;
				return sqlParameterValue.getValue();
			} else {
				return value;
			}
		}).toArray();
	}

	/**
	 * Get parameter type that java type to SQL parameter type.
	 * For {@link org.springframework.jdbc.core.JdbcTemplate}
	 *
	 * @since 1.4.0
	 * @param values parameter
	 * @return java type
	 */
	public static int[] getParamType(Object... values) {
		return Arrays.stream(values).mapToInt(value -> {
			if (value != null) {
				return StatementCreatorUtils.javaTypeToSqlParameterType(value.getClass());
			} else {
				return SqlTypeValue.TYPE_UNKNOWN;
			}
		}).toArray();
	}

	/**
	 * Get all id column information. {@link #getColumnsEntity}
	 *
	 * @since 1.4.0
	 * @param clazz table entity class
	 * @return table all column entity
	 */
	public static List<ColumnEntity> getIdsEntity(Class<?> clazz) {
		BaseTable baseTable = getBaseTable(clazz);
		Assert.isTrue(baseTable != null, "It is not a table entity");
		return getColumnsEntity(baseTable, FieldUtils.getFieldsListWithAnnotation(clazz, BaseId.class));
	}

	/**
	 * Get all column information. {@link #getColumnsEntity}
	 *
	 * @since 1.4.0
	 * @param clazz table entity class
	 * @return table all column entity
	 */
	public static List<ColumnEntity> getColumnsEntity(Class<?> clazz) {
		BaseTable baseTable = getBaseTable(clazz);
		Assert.isTrue(baseTable != null, "It is not a table entity");
		return getColumnsEntity(baseTable, FieldUtils.getAllFieldsList(clazz));
	}

	/**
	 * Get all column information.
	 * Ignore by {@link #isIgnoreColumn}
	 *
	 * @since 1.4.0
	 * @param baseTable table entity class
	 * @param fields field in table entity
	 * @return table all column entity
	 */
	public static List<ColumnEntity> getColumnsEntity(BaseTable baseTable, List<Field> fields) {
		return fields.stream()
				.filter(field -> !isIgnoreColumn(field))
				.map(field -> {
					ColumnEntity columnEntity = new ColumnEntity();
					columnEntity.setType(field.getType());
					columnEntity.setFieldName(field.getName());
					columnEntity.setColumnName(getColumnName(baseTable, field, false));
					columnEntity.setId(field.isAnnotationPresent(BaseId.class));
					columnEntity.setAutoIncrement(isAutoIncrement(field));
					return columnEntity;
				}).collect(Collectors.toList());
	}

	private static BaseTable getBaseTable(Class<?> clazz) {
		return AnnotatedElementUtils.findMergedAnnotation(clazz, BaseTable.class);
	}

	private static String getColumnName(BaseTable baseTable, Field field, boolean alias) {
		CaseFormat fieldFormat = baseTable.fieldFormat();
		CaseFormat columnFormat = baseTable.columnFormat();
		BaseColumn baseColumn = field.getAnnotation(BaseColumn.class);
		String name;

		if (baseColumn != null && StringUtils.isNotBlank(baseColumn.name())) {
			name = baseColumn.name();
		} else {
			name = fieldFormat.to(columnFormat, field.getName());
			name = alias ? name.concat(" AS ").concat(field.getName()) : name;
		}
		return name;
	}

	/**
	 * Get all input parameter value for store procedure.
	 * Map by input parameter name.
	 *
	 * @since 2.1.0
	 * @param entity store procedure entity
	 * @return input parameter
	 */
	public static Map<String, Object> getInputParameterValue(Object entity) {
		BaseSp baseSp = getBaseSp(entity.getClass());

		Assert.isTrue(baseSp != null, "It is not a stored procedure entity");

		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(entity.getClass(), BaseInParam.class);
		return fields.stream()
				.collect(HashMap::new, (map, field) -> {
					try {
						String inputParameterName = field.getAnnotation(BaseInParam.class).name();
						String fieldName = field.getName();
						String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
						Method method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName);
						if (method != null) {
							Object value = MethodUtils.invokeMethod(entity, methodName);
							map.put(inputParameterName, value);
						} else {
							// For lombok boolean type field.
							methodName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
							method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName);
							if (method != null) {
								Object value = MethodUtils.invokeMethod(entity, methodName);
								map.put(inputParameterName, value);
							}
						}
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						log.error("Entity access error: ", e);
						throw new RuntimeException(e);
					}
				}, HashMap::putAll);
	}

	/**
	 * Get all output parameter value for store procedure.
	 * Map by output parameter name.
	 *
	 * @since 2.1.0
	 * @param entity store procedure entity
	 * @return output parameter
	 */
	public static Map<String, Object> getOutputParameterValue(Object entity) {
		BaseSp baseSp = getBaseSp(entity.getClass());

		Assert.isTrue(baseSp != null, "It is not a stored procedure entity");

		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(entity.getClass(), BaseOutParam.class);
		return fields.stream()
				.collect(HashMap::new, (map, field) -> {
					try {
						String outputParameterName = field.getAnnotation(BaseOutParam.class).name();
						String fieldName = field.getName();
						String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
						Method method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName);
						if (method != null) {
							Object value = MethodUtils.invokeMethod(entity, methodName);
							map.put(outputParameterName, transParamValue(field, value));
						} else {
							// For lombok boolean type field.
							methodName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
							method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName);
							if (method != null) {
								Object value = MethodUtils.invokeMethod(entity, methodName);
								map.put(outputParameterName, transParamValue(field, value));
							}
						}
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						log.error("Entity access error: ", e);
						throw new RuntimeException(e);
					}
				}, HashMap::putAll);
	}

	/**
	 * Get result set class for stored procedure.
	 *
	 * @since 2.1.0
	 * @param clazz stored procedure entity class
	 * @return schema name
	 */
	public static Map<String, Class<?>> getResultSetClazz(Class<?> clazz) {
		BaseSp baseSp = getBaseSp(clazz);

		Assert.isTrue(baseSp != null, "It is not a stored procedure entity");

		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, BaseResultSet.class);
		return fields.stream()
				.collect(LinkedHashMap::new, (map, field) -> {
					String fieldName = field.getName();
					Class<?> fieldClazz = field.getType();

					Assert.isTrue(List.class.equals(fieldClazz), "Only support type with list");

					Class<?> resultSetClazz = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					map.put(fieldName, resultSetClazz);
				}, LinkedHashMap::putAll);
	}

	/**
	 * Get stored procedure schema name.
	 *
	 * @since 2.1.0
	 * @param clazz stored procedure entity class
	 * @return schema name
	 */
	public static String getStoredProcedureSchemaName(Class<?> clazz) {
		BaseSp baseSp = getBaseSp(clazz);
		String name = null;

		if (baseSp != null) {
			name = baseSp.schema();
		}

		return name;
	}

	/**
	 * Get stored procedure catalog name.
	 *
	 * @since 2.1.0
	 * @param clazz stored procedure entity class
	 * @return catalog name
	 */
	public static String getStoredProcedureCatalogName(Class<?> clazz) {
		BaseSp baseSp = getBaseSp(clazz);
		String name = null;

		if (baseSp != null) {
			name = baseSp.catalog();
		}

		return name;
	}

	/**
	 * Get input parameter name for stored procedure.
	 *
	 * @since 2.1.0
	 * @param clazz stored procedure entity class
	 * @return input parameter name
	 */
	public static Set<String> getInputParameterName(Class<?> clazz) {
		BaseSp baseSp = getBaseSp(clazz);

		Assert.isTrue(baseSp != null, "It is not a stored procedure entity");

		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, BaseInParam.class);
		return fields.stream()
				.map(field -> {
					BaseInParam baseInParam = field.getAnnotation(BaseInParam.class);
					return baseInParam.name();
				}).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Get output parameter name for stored procedure.
	 *
	 * @since 2.1.0
	 * @param clazz stored procedure entity class
	 * @return output parameter name
	 */
	public static Set<String> getOutputParameterName(Class<?> clazz) {
		BaseSp baseSp = getBaseSp(clazz);

		Assert.isTrue(baseSp != null, "It is not a stored procedure entity");

		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, BaseOutParam.class);
		return fields.stream()
				.map(field -> {
					BaseOutParam baseOutParam = field.getAnnotation(BaseOutParam.class);
					return baseOutParam.name();
				}).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Get result set name for stored procedure.
	 *
	 * @since 2.1.0
	 * @param clazz stored procedure entity class
	 * @return result set name
	 */
	public static Set<String> getResultSetName(Class<?> clazz) {
		BaseSp baseSp = getBaseSp(clazz);

		Assert.isTrue(baseSp != null, "It is not a stored procedure entity");

		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, BaseResultSet.class);
		return fields.stream().map(Field::getName).collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Set output parameter and result set.
	 *
	 * @param entity stored procedure entity
	 * @param values mapping to stored procedure entity's output parameter and result set
	 * @return stored procedure entity
	 */
	public static <T> T setStoredProcedureFieldsValue(T entity, Map<String, ?> values) {
		List<Field> outParamFields = FieldUtils.getFieldsListWithAnnotation(entity.getClass(), BaseOutParam.class);
		List<Field> resultSetFields = FieldUtils.getFieldsListWithAnnotation(entity.getClass(), BaseResultSet.class);

		outParamFields.stream().forEach(field -> {
			String fieldName = field.getName();
			String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			Class<?> fieldClazz = field.getType();
			Method method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName, fieldClazz);
			try {
				if (method != null) {
					BaseOutParam baseOutParam = field.getAnnotation(BaseOutParam.class);
					MethodUtils.invokeMethod(entity, methodName, values.get(baseOutParam.name()));
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | UnsupportedOperationException e) {
				log.error("Entity access error: ", e);
				throw new RuntimeException(e);
			}
		});

		resultSetFields.stream().forEach(field -> {
			String fieldName = field.getName();
			String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			Class<?> fieldClazz = field.getType();
			Method method = MethodUtils.getMatchingAccessibleMethod(entity.getClass(), methodName, fieldClazz);
			try {
				if (method != null) {
					MethodUtils.invokeMethod(entity, methodName, values.get(fieldName));
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | UnsupportedOperationException e) {
				log.error("Entity access error: ", e);
				throw new RuntimeException(e);
			}
		});

		return entity;
	}

	private static BaseSp getBaseSp(Class<?> clazz) {
		return AnnotatedElementUtils.findMergedAnnotation(clazz, BaseSp.class);
	}
}
