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

package net.cogzmc.engine.gearz.command;

import net.cogzmc.engine.gearz.GearzBungee;
import net.cogzmc.engine.gearz.player.bungee.GearzPlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

/**
 * {@link BaseReceiver}, gets all incoming {@link NetCommand} calls from this base {@link net.cogzmc.engine.util.TPluginBungee}.
 */
public class BaseReceiver {
    @NetCommandHandler(args = {"player", "server"}, name = "send")
    public void onSend(HashMap<String, Object> args) {
        Object p = args.get("player");
        Object s = args.get("server");
        if (!(p instanceof String) || !(s instanceof String)) return;
        String player = (String) p;
        String server = (String) s;
        ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(player);
        if (player1 == null) return;
        GearzBungee.connectPlayer(player1, server);
    }

    @NetCommandHandler(args = {"name"}, name = "update_p")
    public void onUpdate(HashMap<String, Object> args) {
        Object p = args.get("player");
        if (!(p instanceof String)) return;
        String player = (String) p;
        ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(player);
        if (player1 == null) return;
        GearzPlayerManager.getInstance().storePlayer(player1);
    }
}
