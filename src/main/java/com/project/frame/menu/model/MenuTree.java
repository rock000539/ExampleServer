/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.menu.model;

import com.bi.base.model.enums.YesNo;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author Allen Lin
 * @since 1.0.0
 */
@Data
public class MenuTree implements Serializable, Menu {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

	private String icon;

	private String parentCode;

	private String url;

	private int order;

	private YesNo leafYn;

	private List<MenuTree> nodes;
}
