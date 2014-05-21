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

package net.cogzmc.engine.gearz.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.cogzmc.engine.gearz.event.GearzEvent;
import net.cogzmc.engine.gearz.game.GearzGame;
import net.cogzmc.engine.gearz.player.GearzPlayer;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/6/13
 * Time: 9:18 PM
 * To change this template use File | Settings | File Templates.
 */
@AllArgsConstructor
public final class PlayerGameEnterEvent extends GearzEvent {
    @Getter
    private final GearzGame game;
    @Getter
    private final GearzPlayer player;
}