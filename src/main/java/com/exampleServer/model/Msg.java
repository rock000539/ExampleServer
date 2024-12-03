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
@Table(name = "MSG")
public class Msg {

	@Id
	@Column(name = "MSG_ID")
	private String msgId;

	@NotNull
	@Column(name = "IDN")
	private String idn;

	@NotNull
	@Column(name = "CONTENT")
	private String content;

	@NotNull
	@Column(name = "CREATE_DT")
	private Date createDt;

	@Column(name = "CREATE_BY")
	private Date createBy;

	@Column(name = "MODIFY_DT")
	private Date modifyDt;

	@Column(name = "MODIFY_BY")
	private String modifyBy;
}
