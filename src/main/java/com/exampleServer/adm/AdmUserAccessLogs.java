/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.exampleServer.adm;

import com.bi.base.database.annotation.BaseColumn;
import com.bi.base.database.annotation.BaseId;
import com.bi.base.database.annotation.BaseTable;
import com.bi.base.database.model.BaseTableObject;
import java.util.Date;
import lombok.Data;

/**
 * 系統操作記錄
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Data
@BaseTable(name = "ADM_USER_ACCESS_LOGS")
public class AdmUserAccessLogs extends BaseTableObject {

	@BaseId
	@BaseColumn(name = "BRAN_CODE")
	private String branCode;

	@BaseColumn(name = "USER_CODE")
	private String userCode;

	@BaseColumn(name = "CUS_CODE")
	private String cusCode;

	@BaseColumn(name = "DEPUTY_USER_CODE")
	private String deputyUserCode;

	@BaseColumn(name = "PROG_CLASSNAME")
	private String progClassName;

	@BaseColumn(name = "METHOD_NAME")
	private String methodName;

	@BaseColumn(name = "DOC_ID")
	private String docId;

	@BaseColumn(name = "REMOTE_IP")
	private String remoteIp;

	@BaseColumn(name = "LOG_DT")
	private Date logDt;

	@BaseColumn(name = "LOGOUT_YN")
	private String logoutYn;
}
