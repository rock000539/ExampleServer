package com.exampleServer.adm.dao;

import com.bi.base.database.dao.BaseDao;
import com.exampleServer.adm.model.AdmUser;
import java.util.List;

public interface AdmUserDao extends BaseDao<AdmUser> {

    List<AdmUser> findByUserName(String userName);
}
