/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.jdbc;

import com.bi.base.database.SqlTemplate;
import com.bi.base.database.SqlTemplateProxy;
import com.bi.base.database.SqlUtil;
import com.bi.base.database.annotation.BaseColumn;
import com.bi.base.database.config.DynamicDataSourceConfig;
import com.bi.base.database.datasource.DynamicDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The database access object core.<br>
 * Provides simple access database with SQL.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnBean(DynamicDataSourceConfig.class)
@Repository
public class SqlUtilImpl implements SqlUtil {

	@Value(value = "${spring.database.showSql:true}")
	@Setter
	private boolean showSql;

	@Value(value = "${spring.database.showSqlArg:true}")
	@Setter
	private boolean showSqlArg;

	@Autowired
	private DynamicDataSource dynamicDataSource;

	@Override
	public <T> T findByType(String sql, Class<T> requiredType, Object... params) {
		logSql(sql, params);
		Collection<T> results = getJdbcTemplate().query(sql, new SingleColumnRowMapper<>(requiredType), params);
		return requiredSingleResult(results);
	}

	@Override
	public <T> T findByType(String sql, Class<T> requiredType, int[] paramTypes, Object... params) {
		logSql(sql, params);
		Collection<T> results = getJdbcTemplate().query(sql, params, paramTypes, new SingleColumnRowMapper<>(requiredType));
		return requiredSingleResult(results);
	}

	@Override
	public <T> T findByType(String sql, Class<T> requiredType, Map<String, ?> params) {
		return findByType(sql, requiredType, new MapSqlParameterSource(params));
	}

	@Override
	public <T> T findByType(String sql, Class<T> requiredType, SqlParameterSource params) {
		logSql(sql, params);
		Collection<T> results = getNamedParameterJdbcTemplate().query(sql, params, new SingleColumnRowMapper<>(requiredType));
		return requiredSingleResult(results);
	}

	@Override
	public <T> List<T> findListByType(String sql, Class<T> requiredType, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().query(sql, new SingleColumnRowMapper<>(requiredType), params);
	}

	@Override
	public <T> List<T> findListByType(String sql, Class<T> requiredType, int[] paramTypes, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().query(sql, params, paramTypes, new SingleColumnRowMapper<>(requiredType));
	}

	@Override
	public <T> List<T> findListByType(String sql, Class<T> requiredType, Map<String, ?> params) {
		return findListByType(sql, requiredType, new MapSqlParameterSource(params));
	}

	@Override
	public <T> List<T> findListByType(String sql, Class<T> requiredType, SqlParameterSource params) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().query(sql, params, new SingleColumnRowMapper<>(requiredType));
	}

	@Override
	public <T> T findOne(String sql, Class<T> clazz, Object... params) {
		logSql(sql, params);
		Collection<T> results = getJdbcTemplate().query(sql, new BaseBeanPropertyRowMapper<>(clazz), params);
		return requiredSingleResult(results);
	}

	@Override
	public <T> T findOne(String sql, Class<T> clazz, int[] paramTypes, Object... params) {
		logSql(sql, params);
		Collection<T> results = getJdbcTemplate().query(sql, params, paramTypes, new BaseBeanPropertyRowMapper<>(clazz));
		return requiredSingleResult(results);
	}

	@Override
	public <T> T findOne(String sql, Class<T> clazz, Map<String, ?> params) {
		return findOne(sql, clazz, new MapSqlParameterSource(params));
	}

	@Override
	public <T> T findOne(String sql, Class<T> clazz, SqlParameterSource params) {
		logSql(sql, params);
		Collection<T> results = getNamedParameterJdbcTemplate().query(sql, params, new BaseBeanPropertyRowMapper<>(clazz));
		return requiredSingleResult(results);
	}

	@Override
	public <T> T findOneWithWrapper(String sql, Class<T> clazz, Object... params) {
		sql = getSqlTemplate().formatTopSql(sql, 1);
		return findOne(sql, clazz, params);
	}

	@Override
	public <T> T findOneWithWrapper(String sql, Class<T> clazz, int[] paramTypes, Object... params) {
		sql = getSqlTemplate().formatTopSql(sql, 1);
		return findOne(sql, clazz, paramTypes, params);
	}

	@Override
	public <T> T findOneWithWrapper(String sql, Class<T> clazz, Map<String, ?> params) {
		return findOneWithWrapper(sql, clazz, new MapSqlParameterSource(params));
	}

	@Override
	public <T> T findOneWithWrapper(String sql, Class<T> clazz, SqlParameterSource params) {
		sql = getSqlTemplate().formatTopSql(sql, 1);
		return findOne(sql, clazz, params);
	}

	protected <T> T requiredSingleResult(Collection<T> results) {
		int size = results != null ? results.size() : 0;
		if (size > 1) throw new IncorrectResultSizeDataAccessException(1, size);
		return size == 0 ? null : results.iterator().next();
	}

	@Override
	public <T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, Object... params) {
		sql = getSqlTemplate().formatTopSql(sql, top);
		return find(sql, clazz, params);
	}

	@Override
	public <T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, int[] paramTypes, Object... params) {
		sql = getSqlTemplate().formatTopSql(sql, top);
		return find(sql, clazz, paramTypes, params);
	}

	@Override
	public <T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, Map<String, ?> params) {
		return findTopWithWrapper(sql, top, clazz, new MapSqlParameterSource(params));
	}

	@Override
	public <T> List<T> findTopWithWrapper(String sql, int top, Class<T> clazz, SqlParameterSource params) {
		sql = getSqlTemplate().formatTopSql(sql, top);
		return find(sql, clazz, params);
	}

	@Override
	public <T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, Object... params) {
		long total = countWithWrapper(sql, params);
		List<T> content = new ArrayList<>();
		if (total > 0) {
			pageable = mapSortColumn(pageable, clazz);
			sql = getSqlTemplate().formatPaginateSql(sql, pageable);
			content = find(sql, clazz, params);
		}
		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public <T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, int[] paramTypes, Object... params) {
		long total = countWithWrapper(sql, paramTypes, params);
		List<T> content = new ArrayList<>();
		if (total > 0) {
			pageable = mapSortColumn(pageable, clazz);
			sql = getSqlTemplate().formatPaginateSql(sql, pageable);
			content = find(sql, clazz, paramTypes, params);
		}
		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public <T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, Map<String, ?> params) {
		return find(sql, pageable, clazz, new MapSqlParameterSource(params));
	}

	@Override
	public <T> Page<T> find(String sql, Pageable pageable, Class<T> clazz, SqlParameterSource params) {
		long total = countWithWrapper(sql, params);
		List<T> content = new ArrayList<>();
		if (total > 0) {
			pageable = mapSortColumn(pageable, clazz);
			sql = getSqlTemplate().formatPaginateSql(sql, pageable);
			content = find(sql, clazz, params);
		}
		return new PageImpl<>(content, pageable, total);
	}

	/**
	 * Map pagination sort property with field name.
	 * that change to <code>@BaseColumn</code> name.
	 *
	 * @since 1.3.0
	 * @param pageable pagination parameter information
	 * @param clazz the type that the result object is expected to match
	 * @param <T> generic parameter for clazz
	 * @return pagination parameter
	 */
	protected <T> Pageable mapSortColumn(Pageable pageable, Class<T> clazz) {
		if (pageable.getSort().isSorted()) {
			List<Sort.Order> orders = pageable.getSort().stream().map(sort -> {
				Field field = FieldUtils.getField(clazz, sort.getProperty(), true);
				if (field != null && field.isAnnotationPresent(BaseColumn.class)) {
					String name = field.getAnnotation(BaseColumn.class).name();
					Sort.Direction direction = sort.getDirection();
					if (direction.isAscending()) {
						return Sort.Order.asc(name);
					} else if (direction.isDescending()) {
						return Sort.Order.desc(name);
					}
				}
				return sort;
			}).collect(Collectors.toList());
			return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
		} else {
			return pageable;
		}
    }

	@Override
	public <T> List<T> find(String sql, Class<T> clazz, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().query(sql, new BaseBeanPropertyRowMapper<>(clazz), params);
	}

	@Override
	public <T> List<T> find(String sql, Class<T> clazz, int[] paramTypes, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().query(sql, params, paramTypes, new BaseBeanPropertyRowMapper<>(clazz));
	}

	@Override
	public <T> List<T> find(String sql, Class<T> clazz, Map<String, ?> params) {
		return find(sql, clazz, new MapSqlParameterSource(params));
	}

	@Override
	public <T> List<T> find(String sql, Class<T> clazz, SqlParameterSource params) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().query(sql, params, new BaseBeanPropertyRowMapper<>(clazz));
	}

	@Override
	public Map<String, Object> findMap(String sql, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().queryForMap(sql, params);
	}

	@Override
	public Map<String, Object> findMap(String sql, int[] paramTypes, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().queryForMap(sql, params, paramTypes);
	}

	@Override
	public Map<String, Object> findMap(String sql, Map<String, ?> params) {
		return findMap(sql, new MapSqlParameterSource(params));
	}

	@Override
	public Map<String, Object> findMap(String sql, SqlParameterSource params) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().queryForMap(sql, params);
	}

	@Override
	public List<Map<String, Object>> findList(String sql, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().queryForList(sql, params);
	}

	@Override
	public List<Map<String, Object>> findList(String sql, int[] paramTypes, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().queryForList(sql, params, paramTypes);
	}

	@Override
	public List<Map<String, Object>> findList(String sql, Map<String, ?> params) {
		return findList(sql, new MapSqlParameterSource(params));
	}

	@Override
	public List<Map<String, Object>> findList(String sql, SqlParameterSource params) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().queryForList(sql, params);
	}

	@Override
	public long count(String sql, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().queryForObject(sql, Integer.class, params);
	}

	@Override
	public long count(String sql, int[] paramTypes, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().queryForObject(sql, params, paramTypes, Integer.class);
	}

	@Override
	public long count(String sql, Map<String, ?> params) {
		return count(sql, new MapSqlParameterSource(params));
	}

	@Override
	public long count(String sql, SqlParameterSource params) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().queryForObject(sql, params, Integer.class);
	}

	@Override
	public long countWithWrapper(String sql, Object... params) {
		sql = getSqlTemplate().formatCountWrapperSql(sql);
		return count(sql, params);
	}

	@Override
	public long countWithWrapper(String sql, int[] paramTypes, Object... params) {
		sql = getSqlTemplate().formatCountWrapperSql(sql);
		return count(sql, paramTypes, params);
	}

	@Override
	public long countWithWrapper(String sql, Map<String, ?> params) {
		return countWithWrapper(sql, new MapSqlParameterSource(params));
	}

	@Override
	public long countWithWrapper(String sql, SqlParameterSource params) {
		sql = getSqlTemplate().formatCountWrapperSql(sql);
		return count(sql, params);
	}

	@Override
	public int update(String sql, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().update(sql, params);
	}

	@Override
	public int update(String sql, int[] paramTypes, Object... params) {
		logSql(sql, params);
		return getJdbcTemplate().update(sql, params, paramTypes);
	}

	@Override
	public int update(String sql, Map<String, ?> params) {
		return update(sql, new MapSqlParameterSource(params));
	}

	@Override
	public int update(String sql, SqlParameterSource params) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().update(sql, params);
	}

	@Override
	public int update(String sql, Map<String, ?> params, KeyHolder generatedKeyHolder) {
		return update(sql, new MapSqlParameterSource(params), generatedKeyHolder);
	}

	@Override
	public int update(String sql, SqlParameterSource params, KeyHolder generatedKeyHolder) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().update(sql, params, generatedKeyHolder);
	}

	@Override
	public int[] updateBatch(String sql, List<Object[]> params) {
		logSql(sql, params);
		return getJdbcTemplate().batchUpdate(sql, params);
	}

	@Override
	public int[] updateBatch(String sql, int[] paramTypes, List<Object[]> params) {
		logSql(sql, params);
		return getJdbcTemplate().batchUpdate(sql, params, paramTypes);
	}

	@Override
	public int[] updateBatch(String sql, Map<String, ?>[] params) {
		return updateBatch(sql, SqlParameterSourceUtils.createBatch(params));
	}

	@Override
	public int[] updateBatch(String sql, SqlParameterSource[] params) {
		logSql(sql, params);
		return getNamedParameterJdbcTemplate().batchUpdate(sql, params);
	}

	@Override
	public Map<String, Object> executeSp(String spName) {
		SimpleJdbcCall simpleJdbcCall = getSimpleJdbcCallSp(spName, "", "");
		return executeSp(simpleJdbcCall, null);
	}

	@Override
	public Map<String, Object> executeSp(String spName, Map<String, ?> params) {
		return executeSp(spName, "", "", params);
	}

	@Override
	public Map<String, Object> executeSp(String spName, @Nullable String schemaName, @Nullable String catalogName, Map<String, ?> params) {
		SimpleJdbcCall simpleJdbcCall = getSimpleJdbcCallSp(spName, schemaName, catalogName);
		if (showSqlArg) log.info("Execute SP: params: {}", params.toString());
		return executeSp(simpleJdbcCall, params);
	}

	@Override
	public Map<String, Object> executeSpWithResultSet(String spName, Map<String, Class<?>> resultSetMap) {
		return executeSpWithResultSet(spName, "", "", resultSetMap);
	}

	@Override
	public Map<String, Object> executeSpWithResultSet(String spName, Map<String, ?> params, Map<String, Class<?>> resultSetMap) {
		return executeSpWithResultSet(spName, "", "", params, resultSetMap);
	}

	@Override
	public Map<String, Object> executeSpWithResultSet(String spName, @Nullable String schemaName, @Nullable String catalogName, Map<String, Class<?>> resultSetMap) {
		return executeSpWithResultSet(spName, schemaName, catalogName, Collections.emptyMap(), resultSetMap);
	}

	@Override
	public Map<String, Object> executeSpWithResultSet(String spName, @Nullable String schemaName, @Nullable String catalogName, Map<String, ?> params, Map<String, Class<?>> resultSetMap) {
		SimpleJdbcCall simpleJdbcCall = getSimpleJdbcCallSp(spName, schemaName, catalogName);
		resultSetMap.entrySet().forEach(entry -> {
			Class<?> clazz = entry.getValue();
			if (BeanUtils.isSimpleValueType(clazz)) {
				simpleJdbcCall.returningResultSet(entry.getKey(), new SingleColumnRowMapper<>(clazz));
			} else if (Map.class.equals(clazz)) {
				simpleJdbcCall.returningResultSet(entry.getKey(), new ColumnMapRowMapper());
			} else {
				simpleJdbcCall.returningResultSet(entry.getKey(), new BaseBeanPropertyRowMapper<>(clazz));
			}
		});
		if (showSqlArg) log.info("Execute SP: params: {}", params.toString());
		return executeSp(simpleJdbcCall, params);
	}

	/**
	 * Execute stored procedure
	 *
	 * @since 2.1.0
	 * @param simpleJdbcCall
	 * @param params parameter
	 * @return
	 */
	protected Map<String, Object> executeSp(SimpleJdbcCall simpleJdbcCall, @Nullable Map<String, ?> params) {
		if (params != null) {
			return simpleJdbcCall.execute(new MapSqlParameterSource(params));
		} else {
			return simpleJdbcCall.execute(new HashMap<>(0));
		}
	}

	/**
	 * Get spring's {@link org.springframework.jdbc.core.simple.SimpleJdbcCall}.
	 *
	 * @since 2.1.0
	 * @param spName stored procedure name
	 * @param schema the schema of the Stored procedure
	 * @param catalog the catalog of the Stored procedure
	 * @return database access object
	 */
	protected SimpleJdbcCall getSimpleJdbcCallSp(String spName, @Nullable String schema, @Nullable String catalog) {
		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(getJdbcTemplate());
		simpleJdbcCall.withProcedureName(spName);
		simpleJdbcCall.setSchemaName(schema);
		simpleJdbcCall.setCatalogName(catalog);
		setDefaultSimpleJdbcCall(simpleJdbcCall);
		if (showSql) log.info("Execute SP: name: {}, schema: {}, catalog: {}", spName, simpleJdbcCall.getSchemaName(), simpleJdbcCall.getCatalogName());
		return simpleJdbcCall;
	}

	/**
	 * Set SimpleJdbcCall default database information.
	 *
	 * @since 2.0.1
	 * @param simpleJdbcCall
	 */
	protected void setDefaultSimpleJdbcCall(SimpleJdbcCall simpleJdbcCall) {
		DataSource dataSource = dynamicDataSource.getDataSource();
		try (Connection connection = dataSource.getConnection()) {
			String schema = connection.getSchema();
			String catalog = connection.getCatalog();
			if (StringUtils.isNotBlank(schema) && StringUtils.isBlank(simpleJdbcCall.getSchemaName())) {
				simpleJdbcCall.withSchemaName(schema);
			}
			if (StringUtils.isNotBlank(catalog) && StringUtils.isBlank(simpleJdbcCall.getCatalogName())) {
				simpleJdbcCall.withCatalogName(catalog);
			}
		} catch (SQLException e) {
			log.warn("Set SimpleJdbcCall error: ", e);
		}
	}

	/**
	 * Get SQL template {@link com.bi.base.database.SqlTemplate}.
	 *
	 * @since 1.4.0
	 * @return SQL format template
	 */
	protected SqlTemplate getSqlTemplate() {
		return SqlTemplateProxy.getSqlTemplate(dynamicDataSource.getDataSource());
	}

	/**
	 * Get spring's {@link org.springframework.jdbc.core.JdbcTemplate}.
	 *
	 * @return database access object
	 */
	protected JdbcTemplate getJdbcTemplate() {
		return dynamicDataSource.getJdbcTemplate();
	}

	/**
	 * Get spring's {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate}.
	 *
	 * @return database access object
	 */
	protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return dynamicDataSource.getNamedParameterJdbcTemplate();
	}

	/**
	 * Logging SQL.
	 *
	 * @param sql SQL to execute
	 */
	private void logSql(String sql) {
		if (showSql) log.info(sql);
	}

	/**
	 * Logging SQL.
	 *
	 * @param sql SQL to execute
	 * @param params SQL to execute parameter
	 */
	private void logSql(String sql, Object... params) {
		logSql(sql);
		if (showSqlArg && ArrayUtils.isNotEmpty(params)) log.info(Arrays.asList(params).toString());
	}

	/**
	 * Logging SQL.
	 *
	 * @param sql SQL to execute
	 * @param params SQL to execute parameter
	 */
	private void logSql(String sql, List<Object[]> params) {
		logSql(sql);
		if (showSqlArg && !CollectionUtils.isEmpty(params)) log.info(params.stream().map(Arrays::asList).collect(Collectors.toList()).toString());
	}

	/**
	 * Logging SQL.
	 *
	 * @since 1.4.0
	 * @param sql SQL to execute
	 * @param params SQL to execute parameter
	 */
	private void logSql(String sql, SqlParameterSource... params) {
		logSql(sql);
		if (showSqlArg && ArrayUtils.isNotEmpty(params)) {
			List<Map<Object, Object>> paramsLogging = Arrays.stream(params)
					.filter(param -> param.getParameterNames() != null)
					.map(param -> Arrays.stream(param.getParameterNames())
							.filter(paramName -> !"class".equals(paramName))
							.collect(HashMap::new, (map, paramName) -> {
								Object value = param.getValue(paramName);
								if (value instanceof SqlParameterValue) {
									map.put(paramName, ((SqlParameterValue) value).getValue());
								} else {
									map.put(paramName, value);
								}
							}, HashMap::putAll))
					.collect(Collectors.toList());
			log.info(paramsLogging.toString());
		}
	}

}
