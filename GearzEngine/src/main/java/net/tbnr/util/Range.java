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
    private final Number x;
    private final Number y;

    private static enum RangeMode {
        XY,
        X
    }

    private Range(Number x, Number y) {
        this.x = x;
        this.y = y;
    }

    private Range(Number x) {
        this(x, -1);
    }

    public static Range in(Number x, Number y) {
        if (x.doubleValue() > y.doubleValue()) {
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

    public boolean isWithinRange(Number x) {
        if (y == -1) {
            return x.doubleValue() >= this.x.doubleValue();
        }
        return x.doubleValue() >= this.x.doubleValue() && x.doubleValue() < this.y.doubleValue();
    }
}
