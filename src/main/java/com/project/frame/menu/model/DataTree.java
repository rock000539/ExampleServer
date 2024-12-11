/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.menu.model;

import com.bi.base.model.enums.YesNo;
import java.util.List;
import lombok.Data;

/**
 * @author Parker
 * @since 1.0.0
 */
@Data
public class DataTree {

	String id;

	String text;

	Integer depths;

	String parentId;

	boolean checked;

	YesNo removeYn;

	boolean leaf;

	List<DataTree> children;
}
