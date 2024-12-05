/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.exampleServer.adm.model;

import com.bi.base.database.annotation.BaseId;
import com.bi.base.database.annotation.BaseTable;
import com.bi.base.database.model.BaseTableObject;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@BaseTable(name = "adm_user")
public class AdmUser extends BaseTableObject {

	@BaseId
	@Column(name = "user_code")
	private String userCode;

	@Column(name = "user_name")
	private String userName;
}
