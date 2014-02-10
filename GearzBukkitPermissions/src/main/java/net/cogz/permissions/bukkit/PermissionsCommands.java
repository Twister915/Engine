package net.cogz.permissions.bukkit;

import net.cogz.permissions.PermGroup;
import net.cogz.permissions.PermPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Jake on 1/24/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class PermissionsCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("player")) {
            if (args.length == 0) {
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                return false;
            }
            PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
            PermPlayer player = permsManager.getPlayer(args[0].toLowerCase());
            switch (args[1]) {
                case "reset":
                case "delete":
                    if (!sender.hasPermission("gearz.permissions.player.delete")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 2) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    String name = player.getName();
                    player.remove();
                    permsManager.onJoin(name);
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.player-deleted"));
                    return false;
                case "set":
                    if (!sender.hasPermission("gearz.permissions.player.set")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length < 3 || args.length > 4) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (player == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player", false));
                        return false;
                    }
                    boolean value = true;
                    if (args.length == 4) {
                        value = Boolean.parseBoolean(args[3]);
                    }
                    String permission = args[2];
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-player-perm", false, new String[]{"<permission>", permission}, new String[]{"<player>", args[0]}, new String[]{"<value>", value + ""}));
                    permsManager.givePermToPlayer(player.getName(), permission, value);
                    break;
                case "remove":
                case "unset":
                    if (!sender.hasPermission("gearz.permissions.player.remove")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (player == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-perm", false, new String[]{"<oermission>", args[2]}, new String[]{"<player>", args[0]}));
                    player.removePermission(args[2]);
                    return false;
                case "check":
                    if (!sender.hasPermission("gearz.permissions.player.check")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (player == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player", false));
                        return false;
                    }
                    if (player.hasPermission(args[2])) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-player-perm-value", false, new String[]{"<player>", args[0]}, new String[]{"<permission>", args[2]}, new String[]{"<value>", true + ""}));
                    } else {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-player-perm-invalid", false, new String[]{"<player>", args[0]}, new String[]{"<permission>", args[2]}));
                    }
                    return false;
                case "perms":
                case "show":
                case "permissions":
                    if (!sender.hasPermission("gearz.permissions.player.show")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 2) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    for (String perm : player.getPermissions()) {
                        String[] split = perm.split(",");
                        sender.sendMessage(split[0] + " : " + split[1]);
                    }
                    return false;
                case "setgroup":
                    if (!sender.hasPermission("gearz.permissions.player.addgroup")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    PermGroup group = permsManager.getGroup(args[2]);
                    if (group == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.added-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
                    player.setGroup(group);
                    return false;
                case "removegroup":
                    if (!sender.hasPermission("gearz.permissions.player.removegroup")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 2) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    PermGroup grp = player.getGroup();
                    if (grp == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
                    player.removeGroup();
                    return false;
                case "prefix":
                    if (!sender.hasPermission("gearz.permissions.player.prefix")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", player.getName()}));
                        player.prefix = null;
                        player.save();
                        return false;
                    }
                    String prefix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
                    player.prefix = prefix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
                    player.save();
                    return false;
                case "suffix":
                    if (!sender.hasPermission("gearz.permissions.player.suffix")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", player.getName()}));
                        player.suffix = null;
                        player.save();
                        return false;
                    }
                    String suffix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
                    player.suffix = suffix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
                    player.save();
                    return false;
                case "tabcolor":
                    if (!sender.hasPermission("gearz.permissions.player.tabcolor")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    player.tabColor = args[2].trim();
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", args[2]}));
                    player.save();
                    return false;
                case "namecolor":
                    if (!sender.hasPermission("gearz.permissions.player.namecolor")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    player.nameColor = args[2].trim();
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", args[2]}));
                    player.save();
                    return false;
                default:
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                    return false;
            }
            return false;
        } else if (command.getName().equalsIgnoreCase("permissions")) {
            if (args.length < 1) {
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                return false;
            }
            if (!sender.hasPermission("gearz.permissions")) {
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                return false;
            }
            switch (args[0]) {
                case "reload":
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.reload"));
                    GearzBukkitPermissions.getInstance().getPermsManager().reload();
                    return false;
                default:
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                    return false;
            }
        } else if (command.getName().equalsIgnoreCase("group")) {
            if (args.length == 0) {
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.bad-args", false));
                return false;
            }
            PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
            PermGroup group = permsManager.getGroup(args[0]);
            switch (args[1]) {
                case "create":
                    if (!sender.hasPermission("gearz.permissions.group.create")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (permsManager.getGroup(args[0]) != null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.duplicate-group", false));
                        return false;
                    }
                    if (args.length < 2 || args.length > 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    boolean defau = false;
                    if (args.length == 3) {
                        defau = Boolean.parseBoolean(args[2]);
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.created-group", false, new String[]{"<group>", args[0]}, new String[]{"<default>", defau + ""}));
                    permsManager.createGroup(args[0], defau);
                    return false;
                case "delete":
                    if (!sender.hasPermission("gearz.permissions.group.delete")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (permsManager.getGroup(args[0]) == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    if (args.length != 2) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.deleted-group", false, new String[]{"<group>", args[0]}));
                    permsManager.deleteGroup(args[0]);
                    return false;
                case "set":
                    if (!sender.hasPermission("gearz.permissions.group.set")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length < 3 || args.length > 4) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (group == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    boolean value = true;
                    if (args.length == 4) {
                        value = Boolean.parseBoolean(args[3]);
                    }
                    String permission = args[2];
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-group-perm", false, new String[]{"<permission>", permission}, new String[]{"<group>", args[0]}, new String[]{"<value>", value + ""}));
                    permsManager.givePermToGroup(group.getName(), permission, value);
                    break;
                case "remove":
                case "unset":
                    if (!sender.hasPermission("gearz.permissions.group.remove")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (group == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-group-perm", false, new String[]{"<oermission>", args[2]}, new String[]{"<group>", args[0]}));
                    permsManager.removeGroupPerm(group, args[2]);
                    return false;
                case "check":
                    if (!sender.hasPermission("gearz.permissions.group.check")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (group == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    if (group.hasPermission(args[2])) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-group-perm-value", false, new String[]{"<group>", args[0]}, new String[]{"<permission>", args[2]}, new String[]{"<value>", true + ""}));
                    } else {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-group-perm-invalid", false, new String[]{"<group>", args[0]}, new String[]{"<permission>", args[2]}));
                    }
                    return false;
                case "perms":
                case "show":
                case "permissions":
                    if (!sender.hasPermission("gearz.permissions.group.show")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 2) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    for (String perm : group.getPermissions()) {
                        String[] split = perm.split(",");
                        sender.sendMessage(split[0] + " : " + split[1]);
                    }
                    return false;
                case "prefix":
                    if (!sender.hasPermission("gearz.permissions.group.prefix")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", group.getName()}));
                        group.prefix = null;
                        group.save();
                        return false;
                    }
                    String prefix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
                    group.prefix = prefix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
                    group.save();
                    return false;
                case "suffix":
                    if (!sender.hasPermission("gearz.permissions.group.suffix")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", group.getName()}));
                        group.suffix = null;
                        group.save();
                        return false;
                    }
                    String suffix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
                    group.suffix = suffix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
                    group.save();
                    return false;
                case "tabcolor":
                    if (!sender.hasPermission("gearz.permissions.group.tabcolor")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    group.tabColor = args[2].trim();
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", args[2]}));
                    group.save();
                    return false;
                case "namecolor":
                    if (!sender.hasPermission("gearz.permissions.group.namecolor")) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                    return false;
                }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    group.nameColor = args[2].trim();
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", args[2]}));
                    group.save();
                    return false;
                case "addinheritance":
                    if (!sender.hasPermission("gearz.permissions.group.inheritance")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    PermGroup toAdd = permsManager.getGroup(args[2]);
                    if (toAdd == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                        return false;
                    }
                    permsManager.addInheritance(group, toAdd);
                    return false;
                case "removeinheritance":
                    if (!sender.hasPermission("gearz.permissions.group.inheritance")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    if (args.length != 3) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                        return false;
                    }
                    PermGroup toRemove = permsManager.getGroup(args[2]);
                    if (toRemove == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                        return false;
                    }
                    permsManager.addInheritance(group, toRemove);
                    return false;
                default:
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.no-permission", false));
                    return false;
            }
            return false;
        }
        return false;
    }
}
