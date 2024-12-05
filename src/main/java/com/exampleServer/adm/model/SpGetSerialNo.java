/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.exampleServer.adm.model;

import com.bi.base.database.annotation.BaseSp;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@BaseSp(name = "sp_get_serial_no")
public class SpGetSerialNo {

	String iSerialCode;

	List<String> results;
}
