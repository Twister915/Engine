package net.tbnr.gearz.game.single;

import net.tbnr.gearz.game.GameManager;
import net.tbnr.gearz.game.classes.GearzAbstractClass;
import net.tbnr.gearz.player.GearzPlayer;

public interface GameManagerConnector<PlayerType extends GearzPlayer, ClassType extends GearzAbstractClass<PlayerType>> {
    void playerConnectedToLobby(PlayerType player, GameManager<PlayerType, ClassType> gameManager);
}
