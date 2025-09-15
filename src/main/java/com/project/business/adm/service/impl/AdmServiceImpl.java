/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.business.adm.service.impl;

import com.project.business.adm.dao.AdmUserDao;
import com.project.business.adm.model.AdmUser;
import com.project.business.adm.service.AdmService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdmServiceImpl implements AdmService {

  private final AdmUserDao admUserDao;

  public AdmServiceImpl(AdmUserDao admUserDao) {
    this.admUserDao = admUserDao;
  }

  @Override
  public AdmUser getAdmUserByCode(String userCode) {
    return admUserDao.findById(userCode);
  }

  @Override
  public List<AdmUser> getAdmUsersByName(String userName) {
    return admUserDao.findByUserName(userName);
  }
}
