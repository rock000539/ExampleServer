/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.menu.util;

import com.project.frame.menu.model.Menu;
import com.project.frame.menu.model.MenuTree;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * Provides menu format generator.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public final class MenuTreeUtil {

	private MenuTreeUtil() {}

	/**
	 * Format menus row data to nested menus.
	 *
	 * @param rows
	 * @return
	 */
	public static List<MenuTree> formatNestedMenus(List<? extends Menu> rows) {
		List<MenuTree> menus = new ArrayList<>();
		// Set root nodes.
		for (int i = 0; i < rows.size(); i++) {
			if (StringUtils.isBlank(rows.get(i).getParentCode())) {
				MenuTree menuTree = new MenuTree();
				BeanUtils.copyProperties(rows.get(i), menuTree);
				menus.add(menuTree);
			}
		}
		// Set child nodes.
		for (MenuTree menu : menus) {
			menu.setNodes(MenuTreeUtil.getNodes(menu.getCode(), rows));
		}
		return menus;
	}

	private static List<MenuTree> getNodes(String id, List<? extends Menu> menus) {
		List<MenuTree> nodes = new ArrayList<>();
		for (Menu menu : menus) {
			if (StringUtils.isNotBlank(menu.getParentCode()) && menu.getParentCode().equals(id)) {
				MenuTree menuTree = new MenuTree();
				BeanUtils.copyProperties(menu, menuTree);
				nodes.add(menuTree);
			}
		}
		for (MenuTree node : nodes) {
			if (StringUtils.isBlank(node.getUrl())) {
				node.setNodes(getNodes(node.getCode(), menus));
			}
		}
		if (nodes.size() == 0) {
			return null;
		} else {
			return nodes.stream()
					.sorted(Comparator.comparing(MenuTree::getOrder))
					.collect(Collectors.toList());
		}
	}
}
