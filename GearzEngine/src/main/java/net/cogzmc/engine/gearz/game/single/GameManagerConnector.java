package net.cogzmc.engine.gearz.game.single;

import net.cogzmc.engine.gearz.game.GameManager;
import net.cogzmc.engine.gearz.game.classes.GearzAbstractClass;
import net.cogzmc.engine.gearz.player.GearzPlayer;

public interface GameManagerConnector<PlayerType extends GearzPlayer, ClassType extends GearzAbstractClass<PlayerType>> {
    void playerConnectedToLobby(PlayerType player, GameManager<PlayerType, ClassType> gameManager);
}
