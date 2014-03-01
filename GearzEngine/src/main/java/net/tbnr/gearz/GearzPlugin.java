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

package net.tbnr.gearz;

import net.tbnr.gearz.arena.Arena;
import net.tbnr.gearz.arena.ArenaManager;
import net.tbnr.gearz.event.game.GameRegisterEvent;
import net.tbnr.gearz.game.GameManager;
import net.tbnr.gearz.game.GameMeta;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.game.single.GameManagerSingleGame;
import net.tbnr.util.TPlugin;
import net.tbnr.util.command.TCommandHandler;
import org.bukkit.Bukkit;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/29/13
 * Time: 1:30 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GearzPlugin extends TPlugin {
    private GameManager gameManager;
    private ArenaManager arenaManager;
    private GameMeta meta;

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }

    protected GameMeta getMeta() {
        return this.meta;
    }

    protected void registerGame(Class<? extends Arena> arenaClass, Class<? extends GearzGame> game) throws GearzException {
        GameMeta meta = game.getAnnotation(GameMeta.class);

        if (meta == null) throw new GearzException("No GameMeta found!");

        //Meta values
        this.meta = meta;


        ///REGISTRATION
        Gearz.getInstance().getLogger().info("Game starting registration! " + meta.longName() + " v" + meta.version() + " by " + meta.author() + "[" + meta.shortName() + "]");

        //Create a new arena and assign it
        this.arenaManager = new ArenaManager(this.meta.key(), arenaClass);
        Gearz.getInstance().getLogger().info("ArenaManager setup!");

        //Make a game register event fire it and check if it's cancelled
        GameRegisterEvent event = new GameRegisterEvent(arenaClass, game, meta, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            Gearz.getInstance().getLogger().info("Game will not setup, plugin blocking this.");
            return;
        }
        if (this.arenaManager.getArenas().size() == 0) throw new GearzException("No Arenas Defined for this gamemode.");
        String game_mode = Gearz.getInstance().getConfig().getString("game_mode");

        //If the game mod is single then register it as a single game
        if (game_mode.equalsIgnoreCase("SINGLE")) {
            this.gameManager = new GameManagerSingleGame(game, this);
        } else {
            throw new GearzException("Invalid defined game mode");
        }

        //if game manager is instance of TCommandHandler then register it's commands
        if (this.gameManager instanceof TCommandHandler) registerCommands((TCommandHandler) this.gameManager);

        //Log that the gamemanager is set up
        Gearz.getInstance().getLogger().info("GameManager setup!");

        //Register the game and events
        Gearz.getInstance().registerGame(this);
        registerEvents(this.gameManager);

    }

    @Override
    public void disable() {
        if (this.gameManager != null) this.gameManager.disable();
    }
}
