/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.project.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 顯示轉換功能-欄位定義
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface TransParamField {}
