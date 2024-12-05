/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.service.impl;

import com.bi.base.database.dao.BaseDao;
import com.bi.base.database.service.BaseService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * This implementation provides a simple business service that quickly access data by object.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@NoArgsConstructor
public class BaseServiceImpl<M extends BaseDao<T>, T> implements BaseService<T> {

	@Autowired
	protected M baseDao;

	/**
	 * For {@link com.bi.base.database.annotation.BaseAutowired} that inject spring bean manually.
	 *
	 * @since 1.3.0
	 * @param baseDao data access object
	 */
	public BaseServiceImpl(M baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public T findById(T entity) {
		return baseDao.findById(entity);
	}

	@Override
	public T findById(Object... params) {
		return baseDao.findById(params);
	}

	@Override
	public boolean exist(T entity) {
		return baseDao.exist(entity);
	}

	@Override
	public boolean existById(Object... params) {
		return baseDao.existById(params);
	}

	@Override
	public List<T> findAll() {
		return baseDao.findAll();
	}

	@Override
	public List<T> findAll(Sort sort) {
		return baseDao.findAll(sort);
	}

	@Override
	public Sort formatSort(Sort sort) {
		return baseDao.formatSort(sort);
	}

	@Override
	public List<T> findAllById(List<T> entities) {
		return baseDao.findAllById(entities);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return baseDao.findAll(pageable);
	}

	@Override
	public long count() {
		return baseDao.count();
	}

	@Override
	public long count(T entity) {
		return baseDao.count(entity);
	}

	@Override
	public int insert(T entity) {
		return baseDao.insert(entity);
	}

	@Override
	public int[] insertBatch(List<T> entities) {
		return baseDao.insertBatch(entities);
	}

	@Override
	public T retrieveInsert(T entity) {
		return baseDao.retrieveInsert(entity);
	}

	@Override
	public List<T> retrieveInsertBatch(List<T> entities) {
		return baseDao.retrieveInsertBatch(entities);
	}

	@Override
	public int update(T entity) {
		return baseDao.update(entity);
	}

	@Override
	public int updateWithNotNull(T entity) {
		return baseDao.updateWithNotNull(entity);
	}

	@Override
	public int[] updateBatch(List<T> entities) {
		return baseDao.updateBatch(entities);
	}

	@Override
	public T save(T entity) {
		return baseDao.save(entity);
	}

	@Override
	public T saveWithNotNull(T entity) {
		return baseDao.saveWithNotNull(entity);
	}

	@Override
	public List<T> save(List<T> entities) {
		return baseDao.save(entities);
	}

	@Override
	public int delete(T entity) {
		return baseDao.delete(entity);
	}

	@Override
	public int deleteById(Object... params) {
		return baseDao.deleteById(params);
	}

	@Override
	public int[] deleteBatch(List<T> entities) {
		return baseDao.deleteBatch(entities);
	}

}
