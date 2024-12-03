/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.exampleServer.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "COMMENT")
public class Comment {

	@Id
	@Column(name = "COM_ID")
	private String comId;

	@NotNull
	@Column(name = "IDN")
	private String idn;

	@NotNull
	@Column(name = "RANK")
	private String rank;

	@NotNull
	@Column(name = "IS_HIDE")
	private boolean isHide;

	// 優點
	@Column(name = "ADVANTAGE")
	private String advantage;

	// 缺點
	@Column(name = "SHORTCOMING")
	private String shortcoming;

	@NotNull
	@Column(name = "CREATE_DT")
	private Date createDt;

	@NotNull
	@Column(name = "CREATE_BY")
	private Date createBy;

	@Column(name = "MODIFY_DT")
	private Date modifyDt;

	@Column(name = "MODIFY_BY")
	private String modifyBy;
}
