package com.eshop.helper;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;

public class SortHelper {

	/**
	 * 升序排序
	 * @param list
	 * @param key
	 */
	public static void sortAsc(List<Record> list, String key) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = 0; j < list.size() - 1 - i; j++) {
				Record current = list.get(j);
				Record next = list.get(j + 1);
				if (current.getDouble(key) > next.getDouble(key)) {
					Record tmp = current;
					current = next;
					next = tmp;
				}
			}
		}
	}
	
}
