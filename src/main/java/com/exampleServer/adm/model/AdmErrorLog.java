/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.exampleServer.adm.model;

import com.bi.base.database.annotation.BaseColumn;
import com.bi.base.database.annotation.BaseId;
import com.bi.base.database.annotation.BaseTable;
import com.bi.base.database.model.BaseTableObject;
import lombok.Data;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
@Data
@BaseTable(name = "ADM_ERROR_LOG")
public class AdmErrorLog extends BaseTableObject {

	private static final long serialVersionUID = 1L;

	@BaseId
	@BaseColumn(name = "ERROR_ID")
	private String errorId;

	@BaseColumn(name = "ERROR_MESSAGE")
	private String errorMessage;

	@BaseColumn(name = "ERROR_BODY")
	private String errorBody;

	@BaseColumn(name = "HOST_IP")
	private String hostIp;

	@BaseColumn(name = "CLIENT_IP")
	private String clientIp;

	@BaseColumn(name = "LOGIN_ID")
	private String loginId;
}
