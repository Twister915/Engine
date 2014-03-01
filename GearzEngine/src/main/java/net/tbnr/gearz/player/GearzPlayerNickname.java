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

package net.tbnr.gearz.player;

import net.tbnr.util.player.TPlayerStorable;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/15/13
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public final class GearzPlayerNickname implements TPlayerStorable {
    private final String value;

    public GearzPlayerNickname(String nick) {
        this.value = nick;
    }

    @Override
    public String getName() {
        return "nickname";
    }

    @Override
    public Object getValue() {
        return value;
    }
}
