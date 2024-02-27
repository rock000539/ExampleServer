/*
 * Copyright (c) 2021 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.frame.menu.model;

/**
 * @author Allen Lin
 * @since 1.0.0
 */
public interface Menu {

	String getCode();

	String getName();

	String getIcon();

	String getParentCode();

	String getUrl();

	int getOrder();
}
