/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.business.adm.dao;

import com.bi.base.database.dao.BaseDao;
import com.project.business.adm.model.AdmUser;
import java.util.List;

public interface AdmUserDao extends BaseDao<AdmUser> {

	List<AdmUser> findByUserName(String userName);
}
