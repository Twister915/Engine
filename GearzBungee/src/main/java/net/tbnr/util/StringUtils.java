/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author Jake
 * @since 5/19/2014
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

    public static <T> String formatList(List<? extends T> list, int max) {
        StringBuilder builder = new StringBuilder();
        int current = 0;
        for (T aValue : list) {
            if (current == max) break;
            builder.append(aValue).append(", ");
            current++;
        }
        if (list.size() > 0) {
            builder.deleteCharAt(builder.length() - 2);
        }
        return builder.toString();
    }

    public static String formatDuration(Long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        return sdf.format(new Date(mills - TimeZone.getDefault().getRawOffset()));
    }
}
