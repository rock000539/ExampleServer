package com.exampleServer.adm.service;

import com.exampleServer.adm.model.AdmUser;

public interface AdmService {

    AdmUser getAdmUserByCode(String userCode);
}
