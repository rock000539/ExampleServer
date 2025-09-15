/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.business.adm.web.controller;

import com.bi.base.web.BaseApi;
import com.project.business.adm.model.AdmUser;
import com.project.business.adm.service.AdmService;
import com.project.frame.annotation.RateLimitAspect;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/adm")
public class AdmController extends BaseApi {

  private final AdmService admService;

  public AdmController(AdmService admService) {
    this.admService = admService;
  }

  @RateLimitAspect
  @PostMapping("/rateLimitTest")
  public String handleRefundRequest() {
    return "Success!!";
  }

  @GetMapping("/admUser")
  public AdmUser getAdmUserByCode(@RequestParam String admUserCode) {
    return admService.getAdmUserByCode(admUserCode);
  }

  @GetMapping("/admUsers")
  public List<AdmUser> getAdmUsersByName(@RequestParam String admUserName) {
    return admService.getAdmUsersByName(admUserName);
  }
}
