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

package net.tbnr.gearz.game;

import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.game.classes.GearzAbstractClass;
import net.tbnr.gearz.game.single.GameManagerConnector;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Listener;

/**
 * Move GameManager into an interface to support multiple types of GameManagers.
 */
public interface GameManager<PlayerType extends GearzPlayer, AbstractClassType extends GearzAbstractClass<PlayerType>> extends Listener {
    public GameMeta getGameMeta();
    public GearzPlugin<PlayerType, AbstractClassType> getPlugin();
    public void beginGame(Integer id) throws GameStartException;
    void gameEnded(GearzGame game);
    public void spawn(PlayerType player);
    @SuppressWarnings("unused")
    public void disable();
    void registerListener(GameManagerConnector<PlayerType, AbstractClassType> connector);
    void removeListener(GameManagerConnector<PlayerType, AbstractClassType> connector);

    GearzGame getRunningGame();
    boolean isIngame();
}
