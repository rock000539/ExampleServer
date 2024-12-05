/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * The interface provides simple data access by object.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public interface BaseDao<T> {

	/**
	 * Get data by table primary key.
	 *
	 * @param entity table entity
	 * @return entity
	 */
	T findById(T entity);

	/**
	 * Get data by table primary key.<br>
	 * Parameter must be fill out all primary key value.<br>
	 * Parameter mapping with {@link com.bi.base.database.annotation.BaseId} by order in table entity.
	 *
	 * @since 1.4.0
	 * @param params parameter to bind to the query, mapping order with field in entity
	 * @return entity
	 */
	T findById(Object... params);

	/**
	 * Get data by table primary key.
	 *
	 * @param entities table entities
	 * @return entities
	 */
	List<T> findAllById(List<T> entities);

	/**
	 * Is exist row data in the table.<br>
	 * Get data by id that if an entity has an id (table's primary key).
	 *
	 * @param entity table entity
	 * @return is exist
	 */
	boolean exist(T entity);

	/**
	 * Is exist row data in the table by table primary key.<br>
	 * Parameter must be fill out all primary key value.<br>
	 * Parameter mapping with {@link com.bi.base.database.annotation.BaseId} by order in table entity.
	 *
	 * @since 2.1.0
	 * @param params parameter to bind to the query, mapping order with field in entity
	 * @return is exist
	 */
	boolean existById(Object... params);

	/**
	 * Get all data from table.
	 *
	 * @return entities
	 */
	List<T> findAll();

	/**
	 * Get all data of sorting from the table.
	 *
	 * @param sort sort parameter information
	 * @return entities
	 */
	List<T> findAll(Sort sort);

	/**
	 * Format sorting properties.<br>
	 * Mapping field to actual table on entity.
	 *
	 * @param sort sort parameter information
	 * @return sort parameter information
	 */
	Sort formatSort(Sort sort);

	/**
	 * Get all data with pagination from the table.
	 *
	 * @param pageable pagination parameter information
	 * @return a page of entities
	 */
	Page<T> findAll(Pageable pageable);

	/**
	 * Count table data.
	 *
	 * @return row size
	 */
	long count();

	/**
	 * Count table data by entity.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entity table entity
	 * @return row size
	 */
	long count(T entity);

	/**
	 * Write data into table.
	 *
	 * @param entity table entity
	 * @return the number of rows affected
	 */
	int insert(T entity);

	/**
	 * Write data into table with batch process.
	 *
	 * @param entities table entities
	 * @return an array of the number of rows affected by each statement
	 */
	int[] insertBatch(List<T> entities);

	/**
	 * Write data into table. retrieve entity.<br>
	 * If entity has auto generate key by database, that will retrieve to entity.
	 *
	 * @since 2.0.0
	 * @param entity table entity
	 * @return entity
	 */
	T retrieveInsert(T entity);

	/**
	 * Write multiple data into table without batch process. retrieve entity.<br>
	 * If entity has auto generate key by database, that will retrieve to entity.
	 *
	 * @since 2.0.0
	 * @param entities table entities
	 * @return entities
	 */
	List<T> retrieveInsertBatch(List<T> entities);

	/**
	 * Update data on existing row in table.
	 *
	 * @param entity table entity
	 * @return the number of rows affected
	 */
	int update(T entity);

	/**
	 * Update data on an existing row in the table.<br>
	 * It will not be updated data to null.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entity table entity
	 * @return the number of rows affected
	 */
	int updateWithNotNull(T entity);

	/**
	 * Update data in batch process on an existing row in the table.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entities table entities
	 * @return an array of the number of rows affected by each statement
	 */
	int[] updateBatch(List<T> entities);

	/**
	 * Update data on an existing row in the table, otherwise write data into table.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entity table entity
	 * @return entity
	 */
	T save(T entity);

	/**
	 * Update data on an existing row in the table, otherwise write data into table.<br>
	 * It will not be updated data to null.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entity table entity
	 * @return entity
	 */
	T saveWithNotNull(T entity);

	/**
	 * Update data on an existing row in the table, otherwise write data into table.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entities table entities
	 * @return entities
	 */
	List<T> save(List<T> entities);

	/**
	 * Delete data on an existing row in the table.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entity table entity
	 * @return the number of rows affected
	 */
	int delete(T entity);

	/**
	 * Delete data by table primary key on an existing row in the table.<br>
	 * Parameter must be fill out all primary key value.<br>
	 * Parameter mapping with {@link com.bi.base.database.annotation.BaseId} by order in table entity.
	 *
	 * @since 1.4.0
	 * @param params parameter to bind to the delete, mapping order with field in entity
	 * @return the number of rows affected
	 */
	int deleteById(Object... params);

	/**
	 * Delete data in batch process on an existing row in the table.<br>
	 * Get data by id that if an entity has an id (table's primary key). or use all column value for condition.
	 *
	 * @param entities table entities
	 * @return an array of the number of rows affected by each statement
	 */
	int[] deleteBatch(List<T> entities);

}
