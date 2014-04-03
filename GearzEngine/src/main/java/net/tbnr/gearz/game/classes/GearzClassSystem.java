package net.tbnr.gearz.game.classes;

import lombok.Data;
import net.tbnr.gearz.player.GearzPlayer;

@Data
public abstract class GearzClassSystem<PlayerType extends GearzPlayer, AbstractClassType extends GearzAbstractClass<PlayerType>> {
    private final Class<? extends AbstractClassType>[] classes;
    private final GearzClassResolver<PlayerType, AbstractClassType> classResolver;
}
