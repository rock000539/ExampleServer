/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.model;

import com.bi.base.database.annotation.BaseGeneratorValue;
import com.bi.base.database.util.EntityUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transfer entity to table entity.<br>
 * For quickly verify entity.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Data
public class TableEntity<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Class<?> entityClazz;

	private String tableName;

	private List<ColumnEntity> columnEntities;

	private List<String> idFieldName;

	private Set<String> autoIncrementFieldName;

	private String columnsSql; // Ex: A, B, C

	private String paramsSql; // Ex: :a, :b, :c

	private String columnsWithAliasSql; // Ex: A AS A1, B AS B1, C AS C1

	private String columnsWithParamSql; // Ex: A = :a, B = :b, C = :c

	private String columnsWithParamNonIdSql; // Ex: A = :a, B = :b, C = :c

	private String whereIdsSql; // Ex: A = :a AND B = :b AND C = :c

	private String whereColumnsSql; // Ex: A = :a AND B = :b AND C = :c

	/**
	 * Initial.
	 *
	 * @param clazz table entity class
	 */
	public TableEntity(Class<T> clazz) {
		entityClazz = clazz;
		tableName = EntityUtil.getTableName(clazz);
		columnEntities = EntityUtil.getColumnsEntity(clazz);
		idFieldName = columnEntities.stream().filter(ColumnEntity::isId).map(ColumnEntity::getFieldName).collect(Collectors.toList());
		autoIncrementFieldName = columnEntities.stream().filter(ColumnEntity::isAutoIncrement).map(ColumnEntity::getFieldName).collect(Collectors.toSet());
		columnsSql = columnEntities.stream().filter(entity -> !entity.isAutoIncrement()).map(ColumnEntity::getColumnName).collect(Collectors.joining(", "));
		paramsSql = columnEntities.stream().filter(entity -> !entity.isAutoIncrement()).map(entity -> ":" + entity.getFieldName()).collect(Collectors.joining(", "));
		columnsWithAliasSql = StringUtils.join(EntityUtil.getColumnsName(clazz, true), ", ");
		columnsWithParamSql = columnEntities.stream().filter(entity -> !entity.isAutoIncrement()).map(entity -> entity.getColumnName() + " = :" + entity.getFieldName()).collect(Collectors.joining(", "));
		columnsWithParamNonIdSql = columnEntities.stream().filter(entity -> !entity.isAutoIncrement() && !entity.isId()).map(entity -> entity.getColumnName() + " = :" + entity.getFieldName()).collect(Collectors.joining(", "));
		whereIdsSql = "WHERE " + columnEntities.stream().filter(ColumnEntity::isId).map(entity -> entity.getColumnName() + " = :" + entity.getFieldName()).collect(Collectors.joining(" AND "));
		whereColumnsSql = "WHERE " + columnEntities.stream().map(entity -> entity.getColumnName() + " = :" + entity.getFieldName()).collect(Collectors.joining(" AND "));
	}

	/**
	 * Check the class is the table entity.
	 *
	 * @return is table entity
	 */
	public boolean isTable() {
		return StringUtils.isNotBlank(tableName) && !CollectionUtils.isEmpty(columnEntities);
	}

	/**
	 * Check the class has the table entity ID.
	 *
	 * @return has ID
	 */
	public boolean hasId() {
		return columnEntities != null && columnEntities.stream().anyMatch(ColumnEntity::isId);
	}

	/**
	 * Check the class has the table entity generator.
	 *
	 * @return has generator
	 */
	public boolean hasGenerator() {
		return AnnotationUtils.isAnnotationDeclaredLocally(BaseGeneratorValue.class, entityClazz);
	}
}
