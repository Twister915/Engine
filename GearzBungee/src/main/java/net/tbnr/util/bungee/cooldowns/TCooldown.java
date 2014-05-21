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

package net.tbnr.util.bungee.cooldowns;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/18/13
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCooldown {
    private final long time_stored;
    private final long length;

    public boolean canContinue() {
        return time_stored + length <= Calendar.getInstance().getTimeInMillis();
    }

    public TCooldown(long length) {
        this.time_stored = Calendar.getInstance().getTimeInMillis();
        this.length = length;
    }

    public TCooldown(Long time_stored, Long length) {
        this.time_stored = time_stored;
        this.length = length;
    }

    public Long getTime_stored() {
        return time_stored;
    }

    public Long getLength() {
        return length;
    }
}
