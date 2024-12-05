package com.exampleServer.adm.service;

import com.exampleServer.adm.model.AdmUser;
import java.util.List;

public interface AdmService {

    AdmUser getAdmUserByCode(String userCode);

    List<AdmUser> getAdmUsersByName(String userName);
}
