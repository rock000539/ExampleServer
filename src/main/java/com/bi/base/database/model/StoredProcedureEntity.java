/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.model;

import com.bi.base.database.util.EntityUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Set;

/**
 * Transfer entity to stored procedure entity.<br>
 * For quickly verify entity.
 *
 * @author Allen Lin
 * @since 2.1.0
 */
@Data
public class StoredProcedureEntity<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Class<?> entityClazz;

	private String storedProcedureName;

	private String schemaName;

	private String catalogName;

	private Set<String> inputParameterName;

	private Set<String> outputParameterName;

	private Set<String> resultSetName;

	/**
	 * Initial.
	 *
	 * @param clazz stored procedure entity class
	 */
	public StoredProcedureEntity(Class<T> clazz) {
		entityClazz = clazz;
		storedProcedureName = EntityUtil.getStoredProcedureName(clazz);
		schemaName = EntityUtil.getStoredProcedureSchemaName(clazz);
		catalogName = EntityUtil.getStoredProcedureCatalogName(clazz);
		inputParameterName = EntityUtil.getInputParameterName(clazz);
		outputParameterName = EntityUtil.getOutputParameterName(clazz);
		resultSetName = EntityUtil.getResultSetName(clazz);
	}

	/**
	 * Check the class is the stored procedure entity.
	 *
	 * @return is stored procedure entity
	 */
	public boolean isStoredProcedure() {
		return StringUtils.isNotBlank(storedProcedureName);
	}

	/**
	 * Check the class has the stored procedure entity input parameter.
	 *
	 * @return has input parameter
	 */
	public boolean hasInputParameter() {
		return !CollectionUtils.isEmpty(EntityUtil.getInputParameterName(entityClazz));
	}

	/**
	 * Check the class has the stored procedure entity output parameter.
	 *
	 * @return has output parameter
	 */
	public boolean hasOutputParameter() {
		return !CollectionUtils.isEmpty(EntityUtil.getOutputParameterName(entityClazz));
	}

	/**
	 * Check the class has the stored procedure entity result set.
	 *
	 * @return has result set
	 */
	public boolean hasResultSet() {
		return !CollectionUtils.isEmpty(EntityUtil.getResultSetName(entityClazz));
	}
}
