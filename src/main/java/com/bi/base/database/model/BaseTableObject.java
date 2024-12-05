/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.model;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Provides the basic implementation that contains the basic
 * fields that most tables have.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Data
public abstract class BaseTableObject implements Serializable {

	private static final long serialVersionUID = 2867517501002403590L;

    private Timestamp createDt = new Timestamp(System.currentTimeMillis());

    private String createBy = "SYS";

    private Timestamp modifyDt;

    private String modifyBy;
}
