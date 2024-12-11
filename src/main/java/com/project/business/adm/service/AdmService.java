/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.business.adm.service;

import com.project.business.adm.model.AdmUser;
import java.util.List;

public interface AdmService {

	AdmUser getAdmUserByCode(String userCode);

	List<AdmUser> getAdmUsersByName(String userName);
}
