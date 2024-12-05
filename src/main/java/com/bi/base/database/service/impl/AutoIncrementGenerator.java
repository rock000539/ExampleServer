/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.service.impl;

import com.bi.base.database.service.Generator;

import java.lang.reflect.Field;

/**
 * This implementation provides auto increment column value by table.
 * 
 * @author Allen Lin
 * @since 1.1.0
 */
public class AutoIncrementGenerator implements Generator {

	@Override
	public Object getValue(Object entity, Field field) {
		return null;
	}
}
