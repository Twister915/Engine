package net.tbnr.gearz.game.classes;

import net.tbnr.gearz.player.GearzPlayer;

public interface GearzClassable<PlayerType extends GearzPlayer, AbstractClassType extends GearzAbstractClass<PlayerType>> {
    AbstractClassType getClassFor(PlayerType player);
    GearzClassResolver<PlayerType, AbstractClassType> getClassResolver();
}
