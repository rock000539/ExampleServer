/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.web.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The object unifies API response data format.<br>
 * Contains enough information for forward end.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class ResultEntity<T> implements Serializable {

	private static final long serialVersionUID = -8214179321451253942L;

	/**
	 * Response status, make a forward end to know the actual processing status.
	 */
	private Integer status = HttpStatus.OK.value();

	/**
	 * Simple message to express this response.
	 */
	private String statusMsg;

	/**
	 * Full message to describe the content.
	 */
	private String statusDesc;

	/**
	 * Response data.
	 */
	private T data;

	/**
	 * Response timestamp.
	 */
	private Date timestamp = new Date();

	public ResultEntity(T data) {
		this.data = data;
	}

	public ResultEntity(String statusMsg, T data) {
		this(statusMsg, null, data);
	}

	public ResultEntity(Integer status, String statusMsg) {
		this(status, statusMsg, null, null);
	}

	public ResultEntity(Integer status, String statusMsg, T data) {
		this(status, statusMsg, null, data);
	}

	public ResultEntity(Integer status, String statusMsg, String statusDesc) {
		this(status, statusMsg, statusDesc, null);
	}

	public ResultEntity(Integer status, String statusMsg, String statusDesc, T data) {
		this.status = status;
		this.statusMsg = statusMsg;
		this.statusDesc = statusDesc;
		this.data = data;
	}

	public ResultEntity(String statusMsg, String statusDesc, T data) {
		this.statusMsg = statusMsg;
		this.statusDesc = statusDesc;
		this.data = data;
	}

}
