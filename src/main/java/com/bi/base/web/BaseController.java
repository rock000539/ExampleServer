/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.web;

import com.bi.base.i18n.model.LocaleMessage;

/**
 * Provides a simple object to unify the all server web interface.<br>
 * This contains an internationalization language function.<br>
 * All the web controller must be extend this object.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public abstract class BaseController extends LocaleMessage {}
