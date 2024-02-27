/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;

public class tempTest<E> {

	@Test
	public void test1() {
		List<Integer> list1 = new ArrayList<>();
		list1.add(1);
		list1.add(2);
		list1.add(3);
		list1.add(4);
		List<Integer> list2 = new ArrayList<>();
		list2.add(2);
		list2.add(4);
		list2.add(6);
		list2.add(8);
		List<Integer> list3 = new ArrayList<>();
		list3.add(3);
		list3.add(4);
		list3.add(5);

		Set<Integer> target = new HashSet<>();
		target.add(4);
		Assert.assertEquals(true, CollectionUtils.isEqualCollection(target, intersection(list1, list2, list3)));
	}

	private Set<Integer> intersection(List<Integer> list1, List<Integer> list2, List<Integer> list3) {
		Set<Integer> result = new HashSet<>();
		List<Integer> allNumbers = new ArrayList<>();
		allNumbers.addAll(list1);
		allNumbers.addAll(list2);
		allNumbers.addAll(list3);

		for (Integer number : allNumbers) {
			if (list1.contains(number) && list2.contains(number) && list3.contains(number)) {
				result.add(number);
			}
		}

		return result;
	}
	// Given 3 lists of integers, find the intersection of those 3 lists. eg
	// input: [1, 2, 3, 4], [2, 4, 6, 8], [3, 4, 5]
	// output: [4]

	private Set<Character> commonCharacters(List<String> strings) {
		Set<Character> result = new HashSet<>();
		StringBuffer bf = new StringBuffer();
		for (String data : strings) {
			bf.append(data);
		}

		List<String> characters = Arrays.asList(bf.toString().split(""));
		for (String str : characters) {
			boolean isExist = true;
			for (String data : strings) {
				if (!data.contains(str)) {
					isExist = false;
				}
			}

			if (isExist) {
				result.add(str.charAt(0));
			}
		}

		return result;
	}
	// input: 'google', 'facebook', 'youtube'
	// output: ['e', 'o']

	@Test
	public void test2() {
		List<String> testList = new ArrayList<>();
		testList.add("google");
		testList.add("facebook");
		testList.add("youtube");

		System.out.println(commonCharacters(testList));
	}
}
