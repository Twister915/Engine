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

package net.tbnr.gearz.game.classes;

import net.tbnr.gearz.player.GearzPlayer;

public interface GearzClassable<PlayerType extends GearzPlayer, AbstractClassType extends GearzAbstractClass<PlayerType>> {
    AbstractClassType getClassFor(PlayerType player);
    GearzClassResolver<PlayerType, AbstractClassType> getClassResolver();
}
