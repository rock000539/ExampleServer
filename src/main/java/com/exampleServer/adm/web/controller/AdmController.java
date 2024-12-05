/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.exampleServer.adm.web.controller;

import com.bi.base.web.BaseApi;
import com.exampleServer.adm.model.AdmUser;
import com.exampleServer.adm.service.AdmService;
import com.frame.annotation.RateLimitAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/adm")
public class AdmController extends BaseApi {

  @Autowired private AdmService admService;

  @RateLimitAspect
  @PostMapping("/rateLimitTest")
  public String handleRefundRequest() {
    return "Success!!";
  }

  @GetMapping("/admUser")
  public AdmUser getAdmUserByCode(@RequestParam String admUserCode) {
    return admService.getAdmUserByCode(admUserCode);
  }
}
