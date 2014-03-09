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

package net.tbnr.gearz.netcommand;

import net.tbnr.gearz.server.Server;
import org.bukkit.entity.Player;

/**
 * Quick Utils class. Use this to send players to other servers.
 */
@SuppressWarnings("UnusedDeclaration")
public final class BouncyUtils {
    public static void sendPlayerToServer(Player player, String server) {
        NetCommand.withName("send").withArg("player", player.getName()).withArg("server", server).send();
    }

    public static void sendPlayerToServer(Player player, Server server) {
        sendPlayerToServer(player, server.getBungee_name());
    }
}
