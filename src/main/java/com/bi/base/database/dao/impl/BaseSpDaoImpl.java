/*
 * Copyright (c) 2022 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.dao.impl;

import com.bi.base.database.SqlTemplateProxy;
import com.bi.base.database.SqlUtil;
import com.bi.base.database.dao.BaseSpDao;
import com.bi.base.database.model.StoredProcedureEntity;
import com.bi.base.database.util.EntityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * This implementation provides simple DAO that quickly access data by object.
 * For stored procedure.
 *
 * @author Allen Lin
 * @since 2.1.0
 */
public class BaseSpDaoImpl<T> extends SqlDaoImpl implements BaseSpDao<T> {

	private final Class<T> entityClazz;

	private final StoredProcedureEntity<T> storedProcedureEntity;

	public BaseSpDaoImpl() {
		this(null);
	}

	/**
	 * Support {@link com.bi.base.database.annotation.BaseAutowired} that inject spring bean manually.
	 *
	 * @param entityClazz stored procedure entity class
	 */
	@SuppressWarnings("unchecked")
	public BaseSpDaoImpl(@Nullable Class<T> entityClazz) {
		this.entityClazz = entityClazz != null ? entityClazz : (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.storedProcedureEntity = new StoredProcedureEntity<>(this.entityClazz);
		Assert.isTrue(this.storedProcedureEntity.isStoredProcedure(), "It is not a stored procedure entity");
	}

	/**
	 * For {@link com.bi.base.database.annotation.BaseAutowired} that inject spring bean manually.
	 *
	 * @param entityClazz stored procedure entity class
	 * @param exeSql SQL execute object
	 * @param sqlTemplateProxy SQL template proxy
	 */
	public BaseSpDaoImpl(Class<T> entityClazz, SqlUtil exeSql, SqlTemplateProxy sqlTemplateProxy) {
		this(entityClazz);
		this.exeSql = exeSql;
		this.sqlTemplateProxy = sqlTemplateProxy;
	}

	@Override
	public T execute() {
		return execute(BeanUtils.instantiateClass(this.entityClazz));
	}

	@Override
	public T execute(T entity) {
		String name = storedProcedureEntity.getStoredProcedureName();
		String schema = storedProcedureEntity.getSchemaName();
		String catalog = storedProcedureEntity.getCatalogName();
		Map<String, ?> result;
		Map<String, Object> inParams = EntityUtil.getInputParameterValue(entity);
		Map<String, Object> outParams = EntityUtil.getOutputParameterValue(entity);
		Map<String, Class<?>> resultSetClazz = EntityUtil.getResultSetClazz(entityClazz);
		Map<String, Object> params = new HashMap<>();
		params.putAll(inParams);
		params.putAll(outParams);
		if (resultSetClazz.size() > 0) {
			result = exeSql.executeSpWithResultSet(name, schema, catalog, params, resultSetClazz);
		} else {
			result = exeSql.executeSp(name, schema, catalog, params);
		}
		EntityUtil.setStoredProcedureFieldsValue(entity, result);
		return entity;
	}
}
