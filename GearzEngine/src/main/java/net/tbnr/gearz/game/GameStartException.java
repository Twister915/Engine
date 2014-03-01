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

package net.tbnr.gearz.game;

import net.tbnr.gearz.GearzException;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 11/19/13
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public final class GameStartException extends GearzException {
    private final GameStartFailureCause cause;

    public GameStartException(String s, GameStartFailureCause cause) {
        super(s);
        this.cause = cause;
    }

    @SuppressWarnings("unused")
    public GameStartFailureCause getFailureCause() {
        return cause;
    }
}
