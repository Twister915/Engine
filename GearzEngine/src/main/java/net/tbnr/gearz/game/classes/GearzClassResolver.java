package net.tbnr.gearz.game.classes;

import lombok.Data;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;

import java.util.Collection;

@Data
public abstract class GearzClassResolver<PlayerType extends GearzPlayer, AbstractClassType extends GearzAbstractClass<PlayerType>> {
    private GearzClassSystem<PlayerType, AbstractClassType> classSystem;
    public abstract Class<? extends AbstractClassType> getClassForPlayer(PlayerType player, GearzGame<PlayerType, AbstractClassType> game);
    public abstract void playerUsedClassFully(PlayerType player, AbstractClassType classUsed, GearzGame<PlayerType, AbstractClassType> game);
    public abstract void gameStarting(Collection<PlayerType> players, GearzGame<PlayerType, AbstractClassType> game);
    public abstract boolean canUseClass(PlayerType player, Class<? extends AbstractClassType> clazz);

    public GearzClassMeta getClassMeta(Class<? extends AbstractClassType> classType) {
        return classType.getAnnotation(GearzClassMeta.class);
    }
}
