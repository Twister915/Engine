package net.tbnr.gearz.netcommand;

import org.bukkit.entity.Player;

/**
 * Quick Utils class. Use this to send players to other servers.
 */
@SuppressWarnings("UnusedDeclaration")
public final class BouncyUtils {
    public static void sendPlayerToServer(Player player, String server) {
        NetCommand.withName("send").withArg("player", player.getName()).withArg("server", server).send();
    }
}
