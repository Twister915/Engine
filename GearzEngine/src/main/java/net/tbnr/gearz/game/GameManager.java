/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.game;

import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Listener;

/**
 * Move GameManager into an interface to support multiple types of GameManagers.
 */
public interface GameManager<PlayerType extends GearzPlayer> extends Listener {
    public GameMeta getGameMeta();

    public GearzPlugin<PlayerType> getPlugin();

    public void beginGame(Integer id) throws GameStartException;

    void gameEnded(GearzGame game);

    public void spawn(PlayerType player);

    @SuppressWarnings("unused")
    public void disable();
}
