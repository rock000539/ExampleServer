package com.exampleServer.adm.service.impl;

import com.exampleServer.adm.dao.AdmUserDao;
import com.exampleServer.adm.model.AdmUser;
import com.exampleServer.adm.service.AdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdmServiceImpl implements AdmService {

    @Autowired
    private AdmUserDao admUserDao;
    @Override
    public AdmUser getAdmUserByCode(String userCode) {
        return admUserDao.findById(userCode);
    }
}