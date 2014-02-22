package net.cogz.gearz.hub.items.warpstar;

import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubModule;
import net.cogz.gearz.hub.annotations.HubModuleMeta;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by George on 23/12/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@HubModuleMeta(
        key = "warpstar"
)
public class WarpStarCommands extends HubModule implements TCommandHandler {
    public WarpStarCommands() {
        super(true, false);
    }

    @TCommand(
            name = "setWarp",
            usage = "/setWarp <Material> <name> <lore>..",
            permission = "gearz.setWarp",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus setWarp(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        Player p = (Player) sender;

        if (args.length < 3) return TCommandStatus.FEW_ARGS;
        Material material;
        try {
            material = Material.getMaterial(args[0].toUpperCase());
        } catch (Exception exception) {
            p.sendMessage(exception.getCause().toString());
            return TCommandStatus.INVALID_ARGS;
        }

        String name = ChatColor.translateAlternateColorCodes('&', args[1]);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(GearzHub.getInstance().compile(args, 2, args.length));

        ConfigurationSection section = GearzHub.getInstance().getConfig().createSection("hub.warps." + ChatColor.stripColor(name).toLowerCase());

        section.set("item", material.name());
        section.set("name", args[1]);
        section.set("lore", lore);

        ConfigurationSection location = section.createSection("location");
        location.set("world", p.getLocation().getWorld().getName());
        location.set("x", p.getLocation().getX());
        location.set("y", p.getLocation().getY());
        location.set("z", p.getLocation().getZ());
        location.set("yaw", p.getLocation().getYaw());
        location.set("pitch", p.getLocation().getPitch());

        section.set("location", location);

        GearzHub.getInstance().getConfig().set("hub.warps." + ChatColor.stripColor(name).toLowerCase(), section);
        GearzHub.getInstance().saveConfig();
        p.sendMessage(ChatColor.GREEN + "Warp set!");
        GearzHub.getInstance().getHubItems().refreshWarpStar();
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "delWarp",
            usage = "/delWarp <name>",
            permission = "gearz.delWarp",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus delWarp(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        ConfigurationSection warp = GearzHub.getInstance().getConfig().getConfigurationSection("hub.warps." + args[0]);
        if (warp == null || args.length != 1) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzHub.getInstance().getConfig().set("hub.warps." + args[0], null);
        GearzHub.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Warp deleted!");
        GearzHub.getInstance().getHubItems().refreshWarpStar();
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "listWarp",
            usage = "/listWarp",
            permission = "gearz.listwarp",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus listWarp(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "Warps:");
        for (String string : GearzHub.getInstance().getConfig().getStringList("hub.warps")) {
            sender.sendMessage(ChatColor.RED + string);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzHub.handleCommandStatus(status, sender);
    }
}

