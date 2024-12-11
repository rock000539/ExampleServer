/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.menu.util;

import com.project.frame.menu.model.DataTree;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
public final class DataTreeUtil {

	private DataTreeUtil() {}

	/**
	 * Format menus row data to nested menus.
	 *
	 * @param rows
	 * @return
	 */
	public static List<? extends DataTree> formatNestedMenus(List<? extends DataTree> rows, Integer minDepths) {
		List<DataTree> menus = new ArrayList<>();
		// Set root nodes.
		for (int i = 0; i < rows.size(); i++) {
			if (minDepths != null && minDepths.equals(rows.get(i).getDepths())) {
				menus.add(rows.get(i));
			}
		}
		// Set child nodes.
		for (DataTree menu : menus) {
			List<DataTree> nodes = DataTreeUtil.getNodes(menu.getId(), rows);
			menu.setChildren(nodes);
			if (CollectionUtils.isEmpty(nodes)) {
				menu.setLeaf(true);
			}
		}
		return menus;
	}

	private static List<DataTree> getNodes(String id, List<? extends DataTree> menus) {
		List<DataTree> nodes = new ArrayList<>();
		for (DataTree menu : menus) {
			if (StringUtils.isNotBlank(menu.getParentId()) && menu.getParentId().equals(id)) {
				nodes.add(menu);
			}
		}
		for (DataTree node : nodes) {
			List<DataTree> childNods = getNodes(node.getId(), menus);
			if (childNods == null) {
				node.setLeaf(true);
			}
			node.setChildren(childNods);
		}

		if (nodes.size() == 0) {
			return null;
		} else {
			return nodes;
		}
	}
}
