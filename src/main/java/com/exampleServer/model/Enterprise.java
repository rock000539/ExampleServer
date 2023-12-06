/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.exampleServer.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "ENTERPRISE")
public class Enterprise {

	@NotNull
	@Id
	@Column(name = "IDN")
	private String idn;

	@NotNull
	@Column(name = "ENT_NAME")
	private String entName;

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
