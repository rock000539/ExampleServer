/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */

package com.bi.base.database.service;

import java.lang.reflect.Field;

/**
 * Provides interface for implementing generator to generate column value.
 *
 * @author Allen Lin
 * @since 1.1.0
 */
public interface Generator {

    /**
     * Generate value by entity field.
     *
     * @param entity table entity
     * @param field field in table entity
     * @return Generate value
     */
    Object getValue(Object entity, Field field);
}
