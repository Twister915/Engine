/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.engine.util;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 18/05/2014
 */
public class StringUtils {
	private StringUtils() {
	}

	public static String replace(String s, String target, String replacement) {
		StringBuilder sb = null;
		int start = 0;
		for (int i; (i = s.indexOf(target, start)) != -1; ) {
			if (sb == null) sb = new StringBuilder();
			sb.append(s, start, i);
			sb.append(replacement);
			start = i + target.length();
		}
		if (sb == null) return s;
		sb.append(s, start, s.length());
		return sb.toString();
	}
}
