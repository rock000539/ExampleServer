/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.web;

import org.springframework.web.bind.annotation.RequestMapping;

import com.bi.base.annotation.ApiVersion;
import com.bi.base.annotation.RestResultWrapper;
import com.bi.base.annotation.condition.RestResultWrapperJson;

import static com.bi.base.annotation.handler.ApiVersioningRequestMappingHandler.REQUEST_MAPPING_API_VARIABLE;

/**
 * Provides an abstract object to control the API version.<br>
 * All API interface must be extend this object to achieve the API
 * version control.<br>
 * This object will provide the default API version and JSON wrapper
 * data format.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@RestResultWrapper({RestResultWrapperJson.class})
@ApiVersion(1)
@RequestMapping("/api/" + REQUEST_MAPPING_API_VARIABLE)
public abstract class BaseApi extends BaseController {}
