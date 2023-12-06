/*
 * Copyright (c) 2020 -Parker.
 * All rights reserved.
 */
package com.frame.handler;

import com.frame.annotation.TransParamCode;

/**
 * i18n文字轉換介面定義
 *
 * @author Parker Huang
 * @since 1.0.0
 */
public interface TransParamService extends TransParam {

	String getDescription(String key, String value, TransParamCode annotation, Object object, Object fieldValue) throws Exception;
}
