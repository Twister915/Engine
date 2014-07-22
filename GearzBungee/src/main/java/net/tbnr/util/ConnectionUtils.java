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

package net.tbnr.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;

public class ConnectionUtils {
    public static void connectPlayer(ProxiedPlayer player1, String server) {
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
        if (serverInfo == null) {
            player1.sendMessage(GearzBungee.getInstance().getFormat("server-not-online", true, true));
            return;
        }
        if (player1.getServer().getInfo().getName().equals(server)) {
            player1.sendMessage(GearzBungee.getInstance().getFormat("already-connected"));
            return;
        }
        player1.sendMessage(GearzBungee.getInstance().getFormat("connecting", true, true));
        player1.connect(serverInfo);
    }
}