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

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/4/13
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Range {
    private final Integer x;
    private final Integer y;

    private static enum RangeMode {
        XY,
        X
    }

    private Range(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    private Range(Integer x) {
        this(x, -1);
    }

    public static Range in(Integer x, Integer y) {
        if (x > y) {
            return null;
        }
        if (x.equals(y)) {
            return null;
        }
        return new Range(x, y);
    }

    public static Range atLeast(Integer x) {
        return new Range(x);
    }

    public boolean isWithinRange(Integer x) {
        if (y == -1) {
            return x >= this.x;
        }
        return x >= this.x && x < this.y;
    }
}
