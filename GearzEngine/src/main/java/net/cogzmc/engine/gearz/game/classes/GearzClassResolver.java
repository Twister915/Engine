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

package net.cogzmc.engine.gearz.game.classes;

import lombok.Data;
import net.cogzmc.engine.gearz.game.GearzGame;
import net.cogzmc.engine.gearz.player.GearzPlayer;

import java.util.Collection;

@Data
public abstract class GearzClassResolver<PlayerType extends GearzPlayer, AbstractClassType extends GearzAbstractClass<PlayerType>> {
    private GearzClassSystem<PlayerType, AbstractClassType> classSystem;
    public abstract Class<? extends AbstractClassType> getClassForPlayer(PlayerType player, GearzGame<PlayerType, AbstractClassType> game);
    public abstract void playerUsedClassFully(PlayerType player, AbstractClassType classUsed, GearzGame<PlayerType, AbstractClassType> game);
    public abstract void gameStarting(Collection<PlayerType> players, GearzGame<PlayerType, AbstractClassType> game);

    public GearzClassMeta getClassMeta(Class<? extends AbstractClassType> classType) {
        return classType.getAnnotation(GearzClassMeta.class);
    }
}
