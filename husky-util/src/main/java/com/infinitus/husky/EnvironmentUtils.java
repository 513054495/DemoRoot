package com.infinitus.husky;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public final class EnvironmentUtils {
	private EnvironmentUtils() {
		//Noop
	}

	private static long TIME_DIFF = 0;

	/**
	 * 获取当前时间.
	 */
	public static Date getCurrentTime() {
		return new Date(System.currentTimeMillis() + TIME_DIFF);
	}

	public static Collection<Long> toIdCollection(String mids) {
		Collection<Long> idCollection = new ArrayList<Long>();
		if (mids != null && mids.length() > 0) {
			String[] ids = mids.split(",");
			if (ids != null && ids.length > 0) {
				for (String id : ids) {
					try {
						idCollection.add(Long.parseLong(id));
					} catch (NumberFormatException e) {
						//ignore;
					}
				}
			}
		}
		return idCollection;
	}

	public static final void setTimeDiff(long diff) {
		TIME_DIFF = diff;
	}

}
