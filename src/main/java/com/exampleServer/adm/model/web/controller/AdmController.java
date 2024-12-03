/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.exampleServer.adm.model.web.controller;

import com.frame.annotation.RateLimitAspect;
import com.frame.web.BaseController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/adm")
public class AdmController extends BaseController {

	@RateLimitAspect
	@PostMapping("/rateLimitTest")
	public String handleRefundRequest() {
		return "Success!!";
	}
}
