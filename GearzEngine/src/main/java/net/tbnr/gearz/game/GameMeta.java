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

import org.bukkit.ChatColor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GameMeta interface. Used to describe a game when creating a game. Must annotate any subclass of GearzGame
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameMeta {
    /**
     * The longer version of the name, such as "Survival Games"
     *
     * @return The long name
     */
    public String longName();

    /**
     * The short, abbreviated name such as "SG".
     *
     * @return The short name.
     */
    public String shortName();

    /**
     * The version of this game.
     *
     * @return The game version.
     */
    public String version();

    /**
     * Whoever developed this game should take credit :D
     *
     * @return The author.
     */
    public String author() default "Twister915";

    /**
     * A description of the game!
     *
     * @return The game description.
     */
    public String description();

    /**
     * PvP mode for the game.
     *
     * @return Pvp Mode.
     */
    public PvPMode pvpMode() default PvPMode.FreeForAll;

    public ChatColor mainColor();

    public ChatColor secondaryColor();

    public String key();

    public int minPlayers();

    public int maxPlayers();

    public PlayerCountMode playerCountMode() default PlayerCountMode.Any;

    public static enum PlayerCountMode {
        Odd,
        Even,
        Any
    }

    public static enum PvPMode {
        FreeForAll,
        NoPvP
    }
}
