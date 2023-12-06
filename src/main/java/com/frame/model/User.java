/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Session User 資訊
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	// 使用者代碼
	private final String userCode;

	// 使用者姓名稱
	private final String userName;

	// 被代理人使用者代碼
	private final String deputyUserCode;

	// 被代理人使用者姓名稱
	private final String deputyUserName;

	// 體系代碼
	private final String buCode;

	// 當前角色代碼
	private final String roleCode;

	// 當前角色名稱
	private final String roleName;

	// 當前組織代碼
	private final String branCode;

	// 當前組織名稱
	private final String branName;

	// 當前組織類別
	private final String branType;

	// 當前組織深度
	private final String strset;

	// 當前職位代碼
	private final String posCode;

	// 當前職位名稱
	private final String posName;

	// 所有擁有職位代碼
	private final List<String> posCodes;

	// 所有擁有分行代碼
	private final List<String> branCodes;

	private final String loginId;

	private final String authCode;
}
