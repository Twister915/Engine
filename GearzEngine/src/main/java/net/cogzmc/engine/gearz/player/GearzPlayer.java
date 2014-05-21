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

package net.cogzmc.engine.gearz.player;

import lombok.*;
import net.cogzmc.engine.gearz.Gearz;
import net.cogzmc.engine.gearz.game.GearzGame;
import net.cogzmc.engine.util.player.TPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Stores data about a player in a Gearz game
 * Includes the TPlayer instance, current username,
 * the player's UUID, and their current game.
 */
@EqualsAndHashCode(of = {"username", "uuid"}, doNotUseGetters = true)
@ToString(exclude = "game")
public class GearzPlayer {
    /**
     * TPlayer instance of this GearzPlayer
     */
    @Getter protected final TPlayer tPlayer;
    /**
     * GearzPlayer's username
     */
    @Getter protected final String username;
    /**
     * GearzPlayer's UUID
     */
    @Getter protected final String uuid;
    /**
     * The current game that a player is in
     */
    @Getter @Setter protected GearzGame game;

    protected GearzPlayer(@NonNull TPlayer player) {
        this.tPlayer = player;
        this.username = player.getPlayerName();
        this.uuid = player.getUuid();
    }

    /**
     * Neatly sends an exception to a player
     * @param t throwable to send to the player
     */
    public void sendException(Throwable t) {
        getTPlayer().sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + t.getMessage());
    }

    /**
     * Gets the Bukkit Player instance of the TPlayer
     * @return the Bukkit Player
     */
    public Player getPlayer() {
        return this.tPlayer.getPlayer();
    }

    /**
     * Returns whether or not the player is actually online.
     * @return if the player is online
     */
    public boolean isValid() {
        Gearz.getInstance().debug("GEARZ DEBUG ---<GearzPlayer|279>--------< isValid has been CAUGHT for: " + this.username + " and it returned: " + getPlayer().getName());
        return Bukkit.getPlayer(getUsername()) != null;
    }

    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }
}
