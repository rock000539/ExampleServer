/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtil {

	public static List<Collection<String>> splitCollection(Collection<String> values, int size) {
		List<Collection<String>> result = new ArrayList<>();

		if (values.size() <= size) {
			result.add(values);
		} else {
			int count = 0;
			Collection<String> subCollection = null;
			for (String s : values) {
				if (subCollection == null) {
					subCollection = new ArrayList<String>();
					result.add(subCollection);
				}
				subCollection.add(s);
				count++;
				if (count == size) {
					count = 0;
					subCollection = null;
				}
			}
		}
		return result;
	}
}
