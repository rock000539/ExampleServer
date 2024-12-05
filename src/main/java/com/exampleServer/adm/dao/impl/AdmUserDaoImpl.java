package com.exampleServer.adm.dao.impl;


import com.bi.base.database.dao.impl.BaseDaoImpl;
import com.exampleServer.adm.dao.AdmUserDao;
import com.exampleServer.adm.model.AdmUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdmUserDaoImpl extends BaseDaoImpl<AdmUser> implements AdmUserDao {}
