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

package net.tbnr.gearz.player;

import lombok.*;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.util.player.TPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * gg
 *
 * @returns no re
 */
@EqualsAndHashCode(of = {"username"}, doNotUseGetters = true)
@ToString(exclude = {"game"})
public class GearzPlayer {
    @Getter protected final TPlayer tPlayer;
    @Getter protected final String username;
    @Getter @Setter protected GearzGame game;

    protected GearzPlayer(@NonNull TPlayer player) {
        this.tPlayer = player;
        this.username = player.getPlayerName();
    }

    public void sendException(Throwable t) {
        getTPlayer().sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + t.getMessage());
    }

    public Player getPlayer() {
        return this.tPlayer.getPlayer();
    }

    public boolean isValid() {
        Player player = this.tPlayer.getPlayer();
        if (Gearz.getInstance().showDebug()) {
            Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzPlayer|279>--------< isValid has been CAUGHT for: " + this.username + " and it returned: " + player);
        }
        return player != null && this.tPlayer.isOnline();
    }
}
