/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.transaction;

import java.io.Serializable;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides transaction configuration stored.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public class AtomikosTransactionHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	@Setter
	@Getter
	private static TransactionManager transactionManager;

	@Setter
	@Getter
	private static UserTransaction transaction;
}
