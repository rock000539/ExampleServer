/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.model;

import com.bi.base.model.enums.YesNo;
import java.util.List;
import lombok.Data;

/**
 * 自定義樹狀資料
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Data
public class DataTree {

	private String id;

	private String text;

	private Integer depths;

	private String parentId;

	private boolean checked;

	private YesNo removeYn;

	private boolean leaf;

	private List<DataTree> children;
}
