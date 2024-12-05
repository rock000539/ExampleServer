/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Provides an implementation of database SQL access.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public interface SqlUtil {

	/**
	 * Query DB object by class. (only support JAVA base object type)
	 *
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findByType(String sql, Class<T> requiredType, Object... params);

	/**
	 * Query DB object by class. (only support JAVA base object type)<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findByType(String sql, Class<T> requiredType, int[] paramTypes, Object... params);

	/**
	 * Query DB object by class. (only support JAVA base object type)
	 *
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findByType(String sql, Class<T> requiredType, Map<String, ?> params);

	/**
	 * Query DB object by class. (only support JAVA base object type)
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findByType(String sql, Class<T> requiredType, SqlParameterSource params);

	/**
	 * Query DB object by class. (only support JAVA base object type)
	 *
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findListByType(String sql, Class<T> requiredType, Object... params);

	/**
	 * Query DB object by class. (only support JAVA base object type)<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findListByType(String sql, Class<T> requiredType, int[] paramTypes, Object... params);

	/**
	 * Query DB object by class. (only support JAVA base object type)
	 *
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findListByType(String sql, Class<T> requiredType, Map<String, ?> params);

	/**
	 * Query DB object by class. (only support JAVA base object type)
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param requiredType the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findListByType(String sql, Class<T> requiredType, SqlParameterSource params);

	/**
	 * Query DB object for one row.
	 *
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOne(String sql, Class<T> clazz, Object... params);

	/**
	 * Query DB object for one row.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOne(String sql, Class<T> clazz, int[] paramTypes, Object... params);

	/**
	 * Query DB object for one row.
	 *
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOne(String sql, Class<T> clazz, Map<String, ?> params);

	/**
	 * Query DB object for one row.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOne(String sql, Class<T> clazz, SqlParameterSource params);

	/**
	 * Query DB object for one row.
	 * It will auto to wrap find one SQL.
	 *
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOneWithWrapper(String sql, Class<T> clazz, Object... params);

	/**
	 * Query DB object for one row.
	 * It will auto to wrap find one SQL.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOneWithWrapper(String sql, Class<T> clazz, int[] paramTypes, Object... params);

	/**
	 * Query DB object for one row.
	 * It will auto to wrap find one SQL.
	 *
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOneWithWrapper(String sql, Class<T> clazz, Map<String, ?> params);

	/**
	 * Query DB object for one row.
	 * It will auto to wrap find one SQL.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entity
	 */
	<T> T findOneWithWrapper(String sql, Class<T> clazz, SqlParameterSource params);

	/**
	 * Query DB object for top row.
	 * It will auto to wrap find top sql.
	 *
	 * @param sql SQL query to execute
	 * @param top fetch size
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, Object... params);

	/**
	 * Query DB object for top row.
	 * It will auto to wrap find top SQL.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param top fetch size
	 * @param clazz the type that the result object is expected to match
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, int[] paramTypes, Object... params);

	/**
	 * Query DB object for top row.
	 * It will auto to wrap find top SQL.
	 *
	 * @param sql SQL query to execute
	 * @param top fetch size
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, Map<String, ?> params);

	/**
	 * Query DB object for top row.
	 * It will auto to wrap find top SQL.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param top fetch size
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return entities
	 */
	<T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, SqlParameterSource params);

	/**
	 * Query DB object for pagination. (contain sort if it sortable)
	 * It will auto to wrap pageable and sortable SQL.
	 *
	 * @param sql SQL query to execute
	 * @param pageable pagination parameter information
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return a page of entities
	 */
	<T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, Object... params);

	/**
	 * Query DB object for pagination. (contain sort if it sortable)
	 * It will auto to wrap pageable and sortable SQL.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param pageable pagination parameter information
	 * @param clazz the type that the result object is expected to match
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return a page of entities
	 */
	<T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, int[] paramTypes, Object... params);

	/**
	 * Query DB object for pagination. (contain sort if it sortable)
	 * It will auto to wrap pageable and sortable SQL.
	 *
	 * @param sql SQL query to execute
	 * @param pageable pagination parameter information
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return a page of entities
	 */
	<T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, Map<String, ?> params);

	/**
	 * Query DB object for pagination. (contain sort if it sortable)
	 * It will auto to wrap pageable and sortable SQL.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param pageable pagination parameter information
	 * @param clazz the type that the result object is expected to match
	 * @param params parameter to bind to the query
	 * @return a page of entities
	 */
	<T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, SqlParameterSource params);

    /**
     * Query DB object for list.
     *
     * @param sql SQL query to execute
     * @param clazz the type that the result object is expected to match
     * @param params parameter to bind to the query
     * @return entities
     */
    <T> List<T> find(String sql, Class<T> clazz, Object... params);

    /**
     * Query DB object for list.<br>
	 * Enhance efficacy.
     *
	 * @since 1.1.0
     * @param sql SQL query to execute
     * @param clazz the type that the result object is expected to match
	 * @param paramTypes the type that the parameter object is expected to match
     * @param params parameter to bind to the query
     * @return entities
     */
    <T> List<T> find(String sql, Class<T> clazz, int[] paramTypes, Object... params);

    /**
     * Query DB object for list.
     *
     * @param sql SQL query to execute
     * @param clazz the type that the result object is expected to match
     * @param params parameter to bind to the query
     * @return entities
     */
	<T> List<T> find(String sql, Class<T> clazz, Map<String, ?> params);

    /**
     * Query DB object for list.
     *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
     * @param sql SQL query to execute
     * @param clazz the type that the result object is expected to match
     * @param params parameter to bind to the query
     * @return entities
     */
	<T> List<T> find(String sql, Class<T> clazz, SqlParameterSource params);

	/**
	 * Query DB object for map.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return exactly one row
	 */
	Map<String, Object> findMap(String sql, Object... params);

	/**
	 * Query DB object for map.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return exactly one row
	 */
	Map<String, Object> findMap(String sql, int[] paramTypes, Object... params);

	/**
	 * Query DB object for map.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return exactly one row
	 */
	Map<String, Object> findMap(String sql, Map<String, ?> params);

	/**
	 * Query DB object for map.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return exactly one row
	 */
	Map<String, Object> findMap(String sql, SqlParameterSource params);

	/**
	 * Query DB object for list.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return a List that contains a Map per row
	 */
	List<Map<String, Object>> findList(String sql, Object... params);

	/**
	 * Query DB object for list.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return a List that contains a Map per row
	 */
	List<Map<String, Object>> findList(String sql, int[] paramTypes, Object... params);

	/**
	 * Query DB object for list.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return a List that contains a Map per row
	 */
	List<Map<String, Object>> findList(String sql, Map<String, ?> params);

	/**
	 * Query DB object for list.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return a List that contains a Map per row
	 */
	List<Map<String, Object>> findList(String sql, SqlParameterSource params);

	/**
	 * Query DB object for count.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long count(String sql, Object... params);

	/**
	 * Query DB object for count.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long count(String sql, int[] paramTypes, Object... params);

	/**
	 * Query DB object for count.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long count(String sql, Map<String, ?> params);

	/**
	 * Query DB object for count.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long count(String sql, SqlParameterSource params);

	/**
	 * Query DB object for count.
	 * It will auto to wrap count SQL.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long countWithWrapper(String sql, Object... params);

	/**
	 * Query DB object for count.
	 * It will auto to wrap count SQL.<br>
	 * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL query to execute
	 * @param paramTypes the type that the parameter object is expected to match
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long countWithWrapper(String sql, int[] paramTypes, Object... params);

	/**
	 * Query DB object for count.
	 * It will auto to wrap count SQL.
	 *
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long countWithWrapper(String sql, Map<String, ?> params);

	/**
	 * Query DB object for count.
	 * It will auto to wrap count SQL.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL query to execute
	 * @param params parameter to bind to the query
	 * @return row size
	 */
	long countWithWrapper(String sql, SqlParameterSource params);

    /**
     * Modifies DB object. (include insert and update SQL)
     *
	 * @param sql SQL insert/update to execute
     * @param params parameter to bind to the query
     * @return the number of rows affected
     */
    int update(String sql, Object... params);

    /**
     * Modifies DB object. (include insert and update SQL)<br>
     * Enhance efficacy.
	 *
	 * @since 1.1.0
	 * @param sql SQL insert/update to execute
	 * @param paramTypes the type that the parameter object is expected to match
     * @param params parameter to bind to the query
     * @return the number of rows affected
     */
    int update(String sql, int[] paramTypes, Object... params);

    /**
     * Modifies DB object. (include insert and update SQL)
     *
	 * @param sql SQL insert/update to execute
     * @param params parameter to bind to the query
     * @return the number of rows affected
     */
    int update(String sql, Map<String, ?> params);

    /**
     * Modifies DB object. (include insert and update SQL)
     *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
     * @param sql SQL insert/update to execute
     * @param params parameter to bind to the query
     * @return the number of rows affected
     */
    int update(String sql, SqlParameterSource params);

	/**
	 * Modifies DB object.(include insert and update SQL)<br>
	 * Fetch value of auto increment column by <code>KeyHolder</code>.
	 *
	 * @since 1.1.0
	 * @param sql SQL insert/update to execute
	 * @param params parameter to bind to the query
	 * @param generatedKeyHolder keyHolder that will hold the generated keys
	 * @return the number of rows affected
	 */
	int update(String sql, Map<String, ?> params, KeyHolder generatedKeyHolder);

	/**
	 * Modifies DB object.(include insert and update SQL)<br>
	 * Fetch value of auto increment column by <code>KeyHolder</code>.
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL insert/update to execute
	 * @param params parameter to bind to the query
	 * @param generatedKeyHolder keyHolder that will hold the generated keys
	 * @return the number of rows affected
	 */
	int update(String sql, SqlParameterSource params, KeyHolder generatedKeyHolder);

    /**
     * Modifies DB object. (include insert and update SQL)
     *
     * @param sql SQL insert/update to execute
     * @param params parameter to bind to the query
     * @return an array of the number of rows affected by each statement
     */
    int[] updateBatch(String sql, List<Object[]> params);

    /**
     * Modifies DB object. (include insert and update SQL)<br>
     * Enhance efficacy.
	 *
	 * @since 1.1.0
     * @param sql SQL insert/update to execute
	 * @param paramTypes the type that the parameter object is expected to match
     * @param params parameter to bind to the query
     * @return an array of the number of rows affected by each statement
     */
    int[] updateBatch(String sql, int[] paramTypes, List<Object[]> params);

    /**
     * Modifies DB object. (include insert and update SQL)
     *
     * @param sql SQL insert/update to execute
     * @param params parameter to bind to the query
     * @return an array of the number of rows affected by each statement
     */
    int[] updateBatch(String sql, Map<String, ?>[] params);

	/**
	 * Modifies DB object. (include insert and update SQL)
	 *
	 * @since 1.4.0
	 * @see MapSqlParameterSource
	 * @see BeanPropertySqlParameterSource
	 * @param sql SQL insert/update to execute
	 * @param params parameter to bind to the query
	 * @return an array of the number of rows affected by each statement
	 */
	int[] updateBatch(String sql, SqlParameterSource[] params);

	/**
     * Execute stored procedure.
     *
     * @param spName stored procedure name
     * @return exactly one row
     */
    Map<String, Object> executeSp(String spName);

	/**
     * Execute stored procedure.
     *
     * @param spName stored procedure name
     * @param params parameter
     * @return exactly one row
     */
    Map<String, Object> executeSp(String spName, Map<String, ?> params);

	/**
	 * Execute stored procedure.
	 *
	 * @since 2.1.0
	 * @param spName stored procedure name
	 * @param schemaName the schema of the Stored procedure
	 * @param catalogName the catalog of the Stored procedure
	 * @param params parameter
	 * @return exactly one row
	 */
	Map<String, Object> executeSp(String spName, @Nullable String schemaName, @Nullable String catalogName, Map<String, ?> params);

	/**
	 * Execute stored procedure.
	 *
	 * @since 2.1.0
	 * @param spName stored procedure name
	 * @param resultSetMap the key name with result object class in map
	 * @return exactly one row
	 */
	Map<String, Object> executeSpWithResultSet(String spName, Map<String, Class<?>> resultSetMap);

	/**
	 * Execute stored procedure.
	 *
	 * @since 2.1.0
	 * @param spName stored procedure name
	 * @param params parameter
	 * @param resultSetMap the key name with result object class in map
	 * @return exactly one row
	 */
	Map<String, Object> executeSpWithResultSet(String spName, Map<String, ?> params, Map<String, Class<?>> resultSetMap);

	/**
	 * Execute stored procedure.
	 *
	 * @since 2.1.0
	 * @param spName stored procedure name
	 * @param schemaName the schema of the Stored procedure
	 * @param catalogName the catalog of the Stored procedure
	 * @param resultSetMap the key name with result object class in map
	 * @return exactly one row
	 */
	Map<String, Object> executeSpWithResultSet(String spName, @Nullable String schemaName, @Nullable String catalogName, Map<String, Class<?>> resultSetMap);

	/**
	 * Execute stored procedure.
	 *
	 * @since 2.1.0
	 * @param spName stored procedure name
	 * @param schemaName the schema of the Stored procedure
	 * @param catalogName the catalog of the Stored procedure
	 * @param params parameter
	 * @param resultSetMap the key name with result object class in map
	 * @return exactly one row
	 */
	Map<String, Object> executeSpWithResultSet(String spName, @Nullable String schemaName, @Nullable String catalogName, Map<String, ?> params, Map<String, Class<?>> resultSetMap);

}
