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

import lombok.*;
import net.cogzmc.engine.gearz.event.GearzEvent;
import net.cogzmc.engine.gearz.game.GearzGame;
import net.cogzmc.engine.gearz.player.GearzPlayer;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public final class PlayerGameRespawnEvent extends GearzEvent {
    @Setter(AccessLevel.NONE)
    private GearzPlayer player;
    @Setter(AccessLevel.NONE)
    private GearzGame game;
}
