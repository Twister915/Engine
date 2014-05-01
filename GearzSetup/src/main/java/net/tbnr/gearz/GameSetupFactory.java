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

package net.tbnr.gearz;

import net.tbnr.gearz.arena.Arena;
import net.tbnr.gearz.arena.ArenaCollection;
import net.tbnr.gearz.arena.ArenaManager;
import net.tbnr.gearz.event.game.GameRegisterEvent;
import net.tbnr.gearz.game.GameLobby;
import net.tbnr.gearz.game.GameMeta;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/29/13
 * Time: 1:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameSetupFactory implements Listener, TCommandHandler {
    private final HashMap<GameMeta, GearzPlugin> plugins;
    private final HashMap<GameMeta, Class<? extends Arena>> arenas;
    private final List<GameMeta> metas;

    public GameSetupFactory() {
        this.arenas = new HashMap<>();
        this.plugins = new HashMap<>();
        this.metas = new ArrayList<>();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onGameRegister(GameRegisterEvent event) {
        event.setCancelled(true);
        GameMeta meta = event.getMeta();
        this.metas.add(meta);
        this.arenas.put(meta, event.getArena());
        this.plugins.put(meta, event.getPlugin());
        GearzSetup.getInstance().getLogger().info("Captured " + meta.longName() + "!");
    }

    @TCommand(
            senders = {TCommandSender.Player},
            description = "Setup start.",
            usage = "/setup <game> <lobby|arena>",
            permission = "gearzsetup.use",
            name = "setup")
    @SuppressWarnings("unused")
    public TCommandStatus setup(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length == 1) {
            Class<?> aClass;
            try {
                aClass = Class.forName(args[0]);
            } catch (ClassNotFoundException e) {
                return TCommandStatus.INVALID_ARGS;
            }
            if (!(aClass.isAssignableFrom(Arena.class))) return TCommandStatus.INVALID_ARGS;
            if (!(aClass.isAnnotationPresent(ArenaCollection.class))) return TCommandStatus.INVALID_ARGS;
            ArenaSetup setup = new ArenaSetup(null, (Class<? extends Arena>) aClass, null, GearzSetup.getInstance().getPlayerManager().getPlayer((Player) sender));
            setup.startSetup();
            return TCommandStatus.SUCCESSFUL;
        }
        if (args.length < 2) return TCommandStatus.HELP;
        boolean setupLobby;
        switch (args[1]) {
            case "lobby":
            case "l":
                setupLobby = true;
                break;
            case "game":
            case "map":
            case "arena":
                setupLobby = false;
                break;
            default:
                return TCommandStatus.HELP;
        }
        GameMeta match = null;
        for (GameMeta meta2 : this.metas) {
            if (meta2.key().equalsIgnoreCase(args[0])) match = meta2;
            if (meta2.shortName().equalsIgnoreCase(args[0])) match = meta2;
            if (match != null) break;
        }
        if (match == null) return TCommandStatus.INVALID_ARGS;
        Class<? extends Arena> arena = setupLobby ? GameLobby.class : this.arenas.get(match);
        ArenaManager manager = this.plugins.get(match).getArenaManager();
        ArenaSetup activeSetup = new ArenaSetup(manager, arena, match, GearzSetup.getInstance().getPlayerManager().getPlayer((Player) sender));
        activeSetup.startSetup();
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GameSetupFactory.handleCommandStatus(status, sender);
    }

    public static void handleCommandStatus(TCommandStatus status, CommandSender sender) {
        switch (status) {
            case PERMISSIONS:
                sender.sendMessage(GearzSetup.getInstance().getFormat("formats.no-permissions"));
                break;
            case INVALID_ARGS:
                sender.sendMessage(GearzSetup.getInstance().getFormat("formats.invalid-args"));
                break;
            case FEW_ARGS:
                sender.sendMessage(GearzSetup.getInstance().getFormat("formats.few-args"));
                break;
            case MANY_ARGS:
                sender.sendMessage(GearzSetup.getInstance().getFormat("formats.many-args"));
                break;
            case WRONG_TARGET:
                sender.sendMessage(GearzSetup.getInstance().getFormat("formats.wrong-target"));
                break;
        }
    }
}
