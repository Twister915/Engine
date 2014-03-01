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

package net.tbnr.gearz;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Joey on 12/19/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@RequiredArgsConstructor
@ToString
public class SkullCursor {
    @NonNull
    private final Integer minimum;
    @NonNull
    private final Integer maximum;
    private Integer cursor = -1;
    private Integer thisSession = 0;
    @NonNull
    private final Integer maxSession;

    public boolean shouldContinue() {
        return (thisSession < maxSession && cursor + minimum <= maximum);
    }

    public Integer getNext() {
        cursor++;
        thisSession++;
        return minimum + cursor;
    }

    public void nextSession() {
        this.thisSession = 0;
    }

    public void reset() {
        nextSession();
        this.cursor = 0;
    }

    public boolean isDone() {
        return this.minimum + cursor >= maximum;
    }
}
