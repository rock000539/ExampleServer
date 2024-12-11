/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.business.adm.dao.impl;

import com.bi.base.database.dao.impl.BaseDaoImpl;
import com.project.business.adm.dao.AdmUserDao;
import com.project.business.adm.model.AdmUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdmUserDaoImpl extends BaseDaoImpl<AdmUser> implements AdmUserDao {

	@Override
	public List<AdmUser> findByUserName(String userName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM adm_user WHERE user_name = :userName ");

		Map<String, Object> params = new HashMap<>();
		params.put("userName", userName);
		return exeSql.find(sql.toString(), AdmUser.class, params);
	}
}
