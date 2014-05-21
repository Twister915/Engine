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

package net.cogzmc.engine.gearz;

import lombok.Getter;
import net.cogzmc.engine.activerecord.GModel;
import net.cogzmc.engine.gearz.arena.Arena;
import net.cogzmc.engine.gearz.arena.ArenaManager;
import net.cogzmc.engine.gearz.event.game.GameRegisterEvent;
import net.cogzmc.engine.gearz.game.GameManager;
import net.cogzmc.engine.gearz.game.GameMeta;
import net.cogzmc.engine.gearz.game.GearzGame;
import net.cogzmc.engine.gearz.game.MinigameMeta;
import net.cogzmc.engine.gearz.game.classes.GearzAbstractClass;
import net.cogzmc.engine.gearz.game.classes.GearzClassResolver;
import net.cogzmc.engine.gearz.game.classes.GearzClassSystem;
import net.cogzmc.engine.gearz.game.classes.MinigameClass;
import net.cogzmc.engine.gearz.game.single.GameManagerSingleGame;
import net.cogzmc.engine.gearz.network.GearzNetworkManagerPlugin;
import net.cogzmc.engine.gearz.network.GearzPlayerProvider;
import net.cogzmc.engine.gearz.player.GearzPlayer;
import net.cogzmc.engine.util.TPlugin;
import net.cogzmc.engine.util.command.TCommandHandler;
import org.bukkit.Bukkit;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/29/13
 * Time: 1:30 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GearzPlugin<PlayerType extends GearzPlayer, ClassType extends GearzAbstractClass<PlayerType>> extends TPlugin {
    @Getter private GameManager<PlayerType, ClassType> gameManager;
    @Getter private ArenaManager arenaManager;
    @Getter private GameMeta meta;
    @Getter private GearzClassSystem<PlayerType, ClassType> classSystem;

    public final boolean isClassEnabled() {
        return classSystem != null;
    }

    protected final void registerGame(Class<? extends Arena> arenaClass, Class<? extends GearzGame<PlayerType, ClassType>> game, GearzClassSystem<PlayerType, ClassType> classSystem) throws GearzException {
        GameMeta meta = game.getAnnotation(GameMeta.class);

        if (meta == null) throw new GearzException("No GameMeta found!");

        //Meta values
        this.meta = meta;


        ///REGISTRATION
        Gearz.getInstance().debug("Game starting registration! " + meta.longName() + " v" + meta.version() + " by " + meta.author() + "[" + meta.shortName() + "]");

        //Create a new arena and assign it
        this.arenaManager = new ArenaManager(this.meta.key(), arenaClass);
        Gearz.getInstance().debug("ArenaManager setup!");

        //Make a game register event fire it and check if it's cancelled
        GameRegisterEvent event = new GameRegisterEvent(arenaClass, game, meta, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            Gearz.getInstance().getLogger().info("Game will not setup, plugin blocking this.");
            return;
        }
        if (this.arenaManager.getArenas().size() == 0) throw new GearzException("No Arenas Defined for this gamemode.");
        String game_mode = Gearz.getInstance().getConfig().getString("game_mode");

        //Setup the class resolver
        this.classSystem = classSystem;

        //If the game mod is single then register it as a single game
        if (game_mode.equalsIgnoreCase("SINGLE")) {
            this.gameManager = new GameManagerSingleGame<>(game, this, getPlayerProvider());
        } else {
            throw new GearzException("Invalid defined game mode");
        }

        //Save all the metas for the class in the database
        if (this.classSystem != null) {
            GearzClassResolver<PlayerType, ClassType> classResolver = this.getClassResolver();
            for (Class<? extends ClassType> aClass : this.classSystem.getClasses()) {
                MinigameClass objectFor = MinigameClass.getObjectFor(this, classResolver.getClassMeta(aClass));
                objectFor.save();
            }
        }

        //if game manager is instance of TCommandHandler then register it's commands
        //noinspection ConstantConditions
        if (this.gameManager instanceof TCommandHandler) registerCommands((TCommandHandler) this.gameManager);

        //Log that the gamemanager is set up
        Gearz.getInstance().debug("GameManager setup!");

		//Save the game in the database
        MinigameMeta model1 = new MinigameMeta(Gearz.getInstance().getMongoDB(), meta);
        List<GModel> many = model1.findMany();

        MinigameMeta model = new MinigameMeta(Gearz.getInstance().getMongoDB(), meta, this.getClass().getName(), game.getName());
        if (many.size() > 0) model.setObjectId(model1.getObjectId());
		model.save();

        //Register the game and events
        Gearz.getInstance().registerGame(this);
        registerEvents(this.gameManager);

        //Notify our subclass
        onGameRegister();
    }

    protected final void registerGame(Class<? extends Arena> arenaClass, Class<? extends GearzGame<PlayerType, ClassType>> game) throws GearzException {
        registerGame(arenaClass, game, null);
    }

    @Override
    public final void disable() {
        if (this.gameManager != null) this.gameManager.disable();
    }

    public GearzClassResolver<PlayerType, ClassType> getClassResolver() {
        if (getClassSystem() == null) return null;
        return getClassSystem().getClassResolver();
    }

    protected abstract GearzPlayerProvider<PlayerType> getPlayerProvider();

    protected abstract GearzNetworkManagerPlugin<PlayerType, ? extends GearzPlayerProvider<PlayerType>> getNetworkManager();

    protected void onGameRegister() {}
}
