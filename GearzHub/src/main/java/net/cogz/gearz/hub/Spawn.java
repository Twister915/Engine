package net.cogz.gearz.hub;

import net.tbnr.util.TPlugin;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.player.TPlayerJoinEvent;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 8/31/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class Spawn implements Listener, TCommandHandler {
    private Location spawn;
    public Spawn() {
        updateSpawn();
    }
    @EventHandler
    @SuppressWarnings("unused")
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(this.getSpawn());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(TPlayerJoinEvent event) {
        event.getPlayer().teleport(GearzHub.getInstance().getSpawn().getSpawn());
    }

    @TCommand(
            name = "setspawn",
            usage = "/setspawn",
            permission = "gearz.setspawn",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus setSpawn(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        Player player = (Player) sender;
        this.setSpawn(player.getLocation());
        GearzHub.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Set spawn to where you are standing!");
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "spawn",
            usage = "/spawn [player]",
            permission = "gearz.spawn",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus spawn(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1 && type == TCommandSender.Console) {
            return TCommandStatus.FEW_ARGS;
        }
        if (args.length == 1 && type == TCommandSender.Player && !sender.hasPermission("gearz.spawn.others")) {
            return TCommandStatus.PERMISSIONS;
        }
        Player toSend;
        if (sender instanceof Player && args.length < 1) {
            toSend = (Player) sender;
        } else {
            toSend = Bukkit.getPlayer(args[0]);
        }
        if (toSend == null) {
            return TCommandStatus.INVALID_ARGS;
        }
        this.sendToSpawn(toSend);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "world",
            usage = "/world <name>",
            permission = "gearz.world",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus world(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            String worlds = "";
            for (World w : Bukkit.getWorlds()) {
                if (sender.hasPermission("gearz.world." + w.getName())) worlds = worlds + w.getName() + ", ";
            }
            if (worlds.length() == 0) {
                worlds = "None!";
            } else {
                worlds = worlds.substring(0, worlds.length() - 2);
            }
            sender.sendMessage(GearzHub.getInstance().getFormat("formats.world-list", true, new String[]{"<worlds>", worlds}));
            return TCommandStatus.SUCCESSFUL;
        }
        if (args.length > 1) return TCommandStatus.MANY_ARGS;
        World w = Bukkit.getWorld(args[0]);
        if (w == null) return TCommandStatus.INVALID_ARGS;
        if (!sender.hasPermission("gearz.world." + w.getName())) return TCommandStatus.PERMISSIONS;
        ((Player) sender).teleport(w.getSpawnLocation());
        TPlayerManager.getInstance().getPlayer((Player) sender).playSound(Sound.ENDERMAN_TELEPORT);
        sender.sendMessage(GearzHub.getInstance().getFormat("formats.world-teleport", true, new String[]{"<world>", w.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    public void sendToSpawn(Player player) {
        player.teleport(this.getSpawn());
    }

    public void setSpawn(Location location) {
        GearzHub.getInstance().getConfig().set("spawn", TPlugin.encodeLocationString(location));
        updateSpawn();
    }

    public Location getSpawn() {
        HubArena arena = GearzHub.getInstance().getArena();
        if (arena != null) {
            return arena.pointToLocation(arena.spawnPoints.random());
        }
        return spawn;
    }

    private void updateSpawn() {
        try {
            spawn = TPlugin.parseLocationString(GearzHub.getInstance().getConfig().getString("spawn"));
        } catch (NullPointerException ex) {
            spawn = null;
            GearzHub.getInstance().getLogger().severe("No spawn point set!");
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzHub.handleCommandStatus(status, sender);
    }
}
