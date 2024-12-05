/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.dao.impl;

import com.bi.base.database.SqlTemplateProxy;
import com.bi.base.database.SqlUtil;
import com.bi.base.database.dao.BaseDao;
import com.bi.base.database.model.TableEntity;
import com.bi.base.database.util.EntityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This implementation provides simple DAO that quickly access data by object.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public class BaseDaoImpl<T> extends SqlDaoImpl implements BaseDao<T> {

	private final Class<T> entityClazz;

	private final TableEntity<T> tableEntity;

	public BaseDaoImpl() {
		this(null);
	}

	/**
	 * Support {@link com.bi.base.database.annotation.BaseAutowired} that inject spring bean manually.
	 *
	 * @since 1.3.0
	 * @param entityClazz table entity class
	 */
	@SuppressWarnings("unchecked")
	public BaseDaoImpl(@Nullable Class<T> entityClazz) {
		this.entityClazz = entityClazz != null ? entityClazz : (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.tableEntity = new TableEntity<>(this.entityClazz);
		Assert.isTrue(this.tableEntity.isTable(), "It is not a table entity");
	}

	/**
	 * For {@link com.bi.base.database.annotation.BaseAutowired} that inject spring bean manually.
	 *
	 * @since 1.3.0
	 * @param entityClazz table entity class
	 * @param exeSql SQL execute object
	 * @param sqlTemplateProxy SQL template proxy
	 */
	public BaseDaoImpl(Class<T> entityClazz, SqlUtil exeSql, SqlTemplateProxy sqlTemplateProxy) {
		this(entityClazz);
		this.exeSql = exeSql;
		this.sqlTemplateProxy = sqlTemplateProxy;
	}

	@Override
	public T findById(T entity) {
		Assert.isTrue(tableEntity.hasId(), "Entity has not any id");

		Map<String, SqlParameterValue> params = EntityUtil.getIdsValue(entity);
		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatSelectBaseSql(tableEntity.getTableName(), tableEntity.getColumnsWithAliasSql(), tableEntity.getWhereIdsSql());

		return exeSql.findOneWithWrapper(sql, entityClazz, params);
	}

	@Override
	public T findById(Object... params) {
		Assert.isTrue(tableEntity.hasId(), "Entity has not any id");
		List<String> idFieldNames = tableEntity.getIdFieldName();
		Assert.isTrue(idFieldNames.size() == params.length, "Parameter size incorrect");

		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatSelectBaseSql(tableEntity.getTableName(), tableEntity.getColumnsWithAliasSql(), tableEntity.getWhereIdsSql());
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		for (int i = 0; i < idFieldNames.size(); i++) {
			mapSqlParameterSource.addValue(idFieldNames.get(i), params[i]);
		}

		return exeSql.findOneWithWrapper(sql, entityClazz, mapSqlParameterSource);
	}

	@Override
	public List<T> findAllById(List<T> entities) {
		return entities.stream().map(this::findById).collect(Collectors.toList());
	}

	@Override
	public boolean exist(T entity) {
		return count(entity) > 0;
	}

	@Override
	public boolean existById(Object... params) {
		Assert.isTrue(tableEntity.hasId(), "Entity has not any id");
		List<String> idFieldNames = tableEntity.getIdFieldName();
		Assert.isTrue(idFieldNames.size() == params.length, "Parameter size incorrect");

		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatCountSql(tableEntity.getTableName(), tableEntity.getWhereIdsSql());
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		for (int i = 0; i < idFieldNames.size(); i++) {
			mapSqlParameterSource.addValue(idFieldNames.get(i), params[i]);
		}

		return exeSql.count(sql, mapSqlParameterSource) > 0;
	}

	@Override
	public List<T> findAll() {
		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatSelectBaseSql(tableEntity.getTableName(), tableEntity.getColumnsWithAliasSql(), "");

		return exeSql.find(sql, entityClazz);
	}

	@Override
	public List<T> findAll(Sort sort) {
		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatSelectBaseSql(tableEntity.getTableName(), tableEntity.getColumnsWithAliasSql(), "");

		sort = formatSort(sort);
		sql = sqlTemplateProxy.getSqlTemplate().formatSortSql(sql, sort);

		return exeSql.find(sql, entityClazz);
	}

	@Override
	public Sort formatSort(Sort sort) {
		List<Order> orders = new ArrayList<>();

		sort.stream().forEach(order -> {
			Direction direction = order.getDirection();
			String property = EntityUtil.getColumnName(entityClazz, order.getProperty(), false);

			Assert.isTrue(StringUtils.isNotBlank(property), "Sort's column not found");

			if (Direction.ASC.equals(direction)) {
				orders.add(Order.asc(property));
			} else if (Direction.DESC.equals(direction)) {
				orders.add(Order.desc(property));
			}
		});

		return Sort.by(orders);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatSelectBaseSql(tableEntity.getTableName(), tableEntity.getColumnsWithAliasSql(), "");
		Sort sort = pageable.getSort();

		if (sort.isSorted()) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), formatSort(sort));
		}

		return exeSql.find(sql, pageable, entityClazz);
	}

	@Override
	public long count() {
		String sql = sqlTemplateProxy.getSqlTemplate().formatCountSql(tableEntity.getTableName(), "");

		return exeSql.count(sql);
	}

	@Override
	public long count(T entity) {
		Map<String, SqlParameterValue> params;
		String conditions;
		if (tableEntity.hasId()) {
			params = EntityUtil.getIdsValue(entity);
			conditions = tableEntity.getWhereIdsSql();
		} else {
			params = EntityUtil.getColumnsValue(entity, null);
			conditions = tableEntity.getWhereColumnsSql();
		}

		String sql = sqlTemplateProxy.getSqlTemplate().formatCountSql(tableEntity.getTableName(), conditions);

		return exeSql.count(sql, params);
	}

	protected int insert(T entity, @Nullable KeyHolder generatedKeyHolder) {
		if (tableEntity.hasGenerator()) EntityUtil.setGeneratorFieldValue(entity);

		Map<String, SqlParameterValue> params = EntityUtil.getColumnsValue(entity, tableEntity.getAutoIncrementFieldName()); // Exclude auto increment.
		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatInsertSql(tableEntity.getTableName(), tableEntity.getColumnsSql(), tableEntity.getParamsSql());

		if (generatedKeyHolder == null) {
			return exeSql.update(sql, params);
		} else {
			return exeSql.update(sql, params, generatedKeyHolder);
		}
	}

	@Override
	public int insert(T entity) {
		return insert(entity, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int[] insertBatch(List<T> entities) {
		List<Map<String, ?>> params = new ArrayList<>();
		String sql = sqlTemplateProxy.getSqlTemplate()
				.formatInsertSql(tableEntity.getTableName(), tableEntity.getColumnsSql(), tableEntity.getParamsSql());

		entities.forEach(entity -> {
			if (tableEntity.hasGenerator()) EntityUtil.setGeneratorFieldValue(entity);
			params.add(EntityUtil.getColumnsValue(entity, tableEntity.getAutoIncrementFieldName())); // Exclude auto increment.
		});

		return exeSql.updateBatch(sql, params.toArray(new HashMap[0]));
	}

	@Override
	public T retrieveInsert(T entity) {
		String autoIncrementColumnName = EntityUtil.getAutoIncrementName(entity.getClass(), true);
		if (autoIncrementColumnName != null) {
			KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			insert(entity, generatedKeyHolder);
			EntityUtil.setAutoIncrementFieldValue(entity, generatedKeyHolder.getKey());
		} else {
			insert(entity);
		}
		return entity;
	}

	@Override
	public List<T> retrieveInsertBatch(List<T> entities) {
		return entities.stream().map(this::retrieveInsert).collect(Collectors.toList());
	}

	@Override
	public int update(T entity) {
		Map<String, SqlParameterValue> params;
		String sql, args, where;
		if (tableEntity.hasId()) {
			args = tableEntity.getColumnsWithParamSql();
			where = tableEntity.getWhereIdsSql();
			params = EntityUtil.getColumnsValue(entity, null);
		} else {
			args = tableEntity.getColumnsWithParamNonIdSql();
			where = tableEntity.getWhereColumnsSql();
			params = EntityUtil.getColumnsValue(entity, null);
		}

		sql = sqlTemplateProxy.getSqlTemplate().formatUpdateSql(tableEntity.getTableName(), args, where);

		return exeSql.update(sql, params);
	}

	@Override
	public int updateWithNotNull(T entity) {
		Map<String, SqlParameterValue> params = EntityUtil.getColumnsValue(entity, null)
				.entrySet().parallelStream().filter(entry -> EntityUtil.getParamValue(entry.getValue()) != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // Exclude value is null.
		Set<String> fieldName = params.keySet();
		String sql, args, where;
		if (tableEntity.hasId()) {
			where = tableEntity.getWhereIdsSql();
		} else {
			where = " WHERE " + tableEntity.getColumnEntities().parallelStream()
					.filter(columnEntity -> fieldName.contains(columnEntity.getFieldName()))
					.map(columnEntity -> columnEntity.getColumnName() + " = :" + columnEntity.getFieldName()).collect(Collectors.joining(" AND "));
		}

		args = tableEntity.getColumnEntities().parallelStream()
				.filter(columnEntity -> fieldName.contains(columnEntity.getFieldName()))
				.map(columnEntity -> columnEntity.getColumnName() + " = :" + columnEntity.getFieldName()).collect(Collectors.joining(", "));

		sql = sqlTemplateProxy.getSqlTemplate().formatUpdateSql(tableEntity.getTableName(), args, where);

		return exeSql.update(sql, params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int[] updateBatch(List<T> entities) {
		List<Map<String, SqlParameterValue>> params = new ArrayList<>();
		String sql, args, where;
		if (tableEntity.hasId()) {
			args = tableEntity.getColumnsWithParamSql();
			where = tableEntity.getWhereIdsSql();
		} else {
			args = tableEntity.getColumnsWithParamNonIdSql();
			where = tableEntity.getWhereColumnsSql();
		}

		sql = sqlTemplateProxy.getSqlTemplate().formatUpdateSql(tableEntity.getTableName(), args, where);
		entities.forEach(entity -> params.add(EntityUtil.getColumnsValue(entity, null)));

		return exeSql.updateBatch(sql, params.toArray(new HashMap[0]));
	}

	@Override
	public T save(T entity) {
		Map<String, SqlParameterValue> values = EntityUtil.getIdsValue(entity);
		T result = values.values().stream().anyMatch(Objects::isNull) ? null : findById(entity);
		if (result != null) {
			update(entity);
		} else {
			entity = retrieveInsert(entity);
		}
		return entity;
	}

	@Override
	public T saveWithNotNull(T entity) {
		Map<String, SqlParameterValue> values = EntityUtil.getIdsValue(entity);
		T result = values.values().stream().anyMatch(Objects::isNull) ? null : findById(entity);
		if (result != null) {
			updateWithNotNull(entity);
			entity = findById(entity);
		} else {
			entity = retrieveInsert(entity);
		}
		return entity;
	}

	@Override
	public List<T> save(List<T> entities) {
		return entities.stream().map(this::save).collect(Collectors.toList());
	}

	@Override
	public int delete(T entity) {
		Map<String, SqlParameterValue> params;
		String sql, where;
		if (tableEntity.hasId()) {
			where = tableEntity.getWhereIdsSql();
			params = EntityUtil.getIdsValue(entity);
		} else {
			where = tableEntity.getWhereColumnsSql();
			params = EntityUtil.getColumnsValue(entity, null);
		}

		sql = sqlTemplateProxy.getSqlTemplate().formatDeleteSql(tableEntity.getTableName(), where);

		return exeSql.update(sql, params);
	}

	@Override
	public int deleteById(Object... params) {
		Assert.isTrue(tableEntity.hasId(), "Entity has not any id");
		List<String> idFieldNames = tableEntity.getIdFieldName();
		Assert.isTrue(idFieldNames.size() == params.length, "Parameter size incorrect");

		String sql = sqlTemplateProxy.getSqlTemplate().formatDeleteSql(tableEntity.getTableName(), tableEntity.getWhereIdsSql());
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		for (int i = 0; i < idFieldNames.size(); i++) {
			mapSqlParameterSource.addValue(idFieldNames.get(i), params[i]);
		}

		return exeSql.update(sql, mapSqlParameterSource);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int[] deleteBatch(List<T> entities) {
		List<Map<String, ?>> params = new ArrayList<>();
		String sql, where;
		if (tableEntity.hasId()) {
			where = tableEntity.getWhereIdsSql();
		} else {
			where = tableEntity.getWhereColumnsSql();
		}

		sql = sqlTemplateProxy.getSqlTemplate().formatDeleteSql(tableEntity.getTableName(), where);

		entities.forEach(entity -> {
			Map<String, SqlParameterValue> map;
			if (tableEntity.hasId()) {
				map = EntityUtil.getIdsValue(entity);
			} else {
				map = EntityUtil.getColumnsValue(entity, null);
			}
			params.add(map);
		});

		return exeSql.updateBatch(sql, params.toArray(new HashMap[0]));
	}
}
