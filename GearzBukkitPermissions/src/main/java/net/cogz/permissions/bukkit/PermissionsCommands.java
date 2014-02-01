package net.cogz.permissions.bukkit;

import net.cogz.permissions.PermGroup;
import net.cogz.permissions.PermPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Jake on 1/24/14.
 */
public class PermissionsCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("group")) {
            if (args.length == 0) return false;
            PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
            PermPlayer player = permsManager.getPlayer(args[0]);
            switch (args[1]) {
                case "reset":
                case "delete":
                    if (!sender.hasPermission("gearz.permissions.player.delete")) return false;
                    if (args.length != 2) return false;
                    String name = player.getName();
                    player.remove();
                    permsManager.onJoin(name);
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.player-deleted"));
                    return false;
                case "set":
                    if (!sender.hasPermission("gearz.permissions.player.set")) return false;
                    if (args.length < 3 || args.length > 4) return false;
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
                    player.addPermission(permission, value);
                    break;
                case "remove":
                case "unset":
                    if (!sender.hasPermission("gearz.permissions.player.remove")) return false;
                    if (args.length != 3) return false;
                    if (player == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-perm", false, new String[]{"<oermission>", args[2]}, new String[]{"<player>", args[0]}));
                    player.removePermission(args[2]);
                    return false;
                case "check":
                    if (!sender.hasPermission("gearz.permissions.player.check")) return false;
                    if (args.length != 3) return false;
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
                    if (!sender.hasPermission("gearz.permissions.player.show")) return false;
                    if (args.length != 2) return false;
                    for (String perm : player.getPermissions()) {
                        String[] split = perm.split(",");
                        sender.sendMessage(split[0] + " : " + split[1]);
                    }
                    return false;
                case "addgroup":
                case "setgroup":
                    if (!sender.hasPermission("gearz.permissions.player.addgroup")) return false;
                    if (args.length != 3) return false;
                    PermGroup group = permsManager.getGroup(args[2]);
                    if (group == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.added-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
                    player.addPlayerToGroup(group);
                    return false;
                case "removegroup":
                    if (!sender.hasPermission("gearz.permissions.player.removegroup")) return false;
                    if (args.length != 3) return false;
                    PermGroup grp = permsManager.getGroup(args[2]);
                    if (grp == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
                    player.removePlayerFromGroup(grp);
                    return false;
                case "prefix":
                    if (!sender.hasPermission("gearz.permissions.player.prefix")) return false;
                    if (args.length < 3) return false;
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", player.getName()}));
                        player.prefix = null;
                        player.save();
                        return false;
                    }
                    String prefix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length);
                    player.prefix = prefix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
                    player.save();
                    return false;
                case "suffix":
                    if (!sender.hasPermission("gearz.permissions.player.suffix")) return false;
                    if (args.length < 3) return false;
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", player.getName()}));
                        player.suffix = null;
                        player.save();
                        return false;
                    }
                    String suffix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length);
                    player.suffix = suffix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
                    player.save();
                    return false;
                case "tabcolor":
                    if (!sender.hasPermission("gearz.permissions.player.tabcolor")) return false;
                    if (args.length != 3) return false;
                    player.tabColor = args[2];
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", args[2]}));
                    player.save();
                    return false;
                case "namecolor":
                    if (!sender.hasPermission("gearz.permissions.player.namecolor")) return false;
                    if (args.length != 3) return false;
                    player.nameColor = args[2];
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", args[2]}));
                    player.save();
                    return false;
                default:
                    return false;
            }
            return false;
        } else if (command.getName().equalsIgnoreCase("permissions")) {
            if (args.length < 1) {
                return false;
            }
            switch (args[0]) {
                case "reload":
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.reload"));
                    GearzBukkitPermissions.getInstance().getPermsManager().reload();
                    return false;
                default:
                    return false;
            }
        } else if (command.getName().equalsIgnoreCase("group")) {
            if (args.length == 0) return false;
            PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
            PermGroup group = permsManager.getGroup(args[0]);
            switch (args[1]) {
                case "create":
                    if (!sender.hasPermission("gearz.permissions.group.create")) return false;
                    if (args.length < 2 || args.length > 3) return false;
                    boolean defau = false;
                    if (args.length == 3) {
                        defau = Boolean.parseBoolean(args[2]);
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.created-group", false, new String[]{"<group>", args[0]}, new String[]{"<default>", defau + ""}));
                    permsManager.createGroup(args[0], defau);
                    return false;
                case "delete":
                    if (!sender.hasPermission("gearz.permissions.group.delete")) return false;
                    if (args.length != 2) return false;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.deleted-group", false, new String[]{"<group>", args[0]}));
                    permsManager.deleteGroup(args[0]);
                    return false;
                case "set":
                    if (!sender.hasPermission("gearz.permissions.group.set")) return false;
                    if (args.length < 3 || args.length > 4) return false;
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
                    group.addPermission(permission, value);
                    break;
                case "remove":
                case "unset":
                    if (!sender.hasPermission("gearz.permissions.group.remove")) return false;
                    if (args.length != 3) return false;
                    if (group == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                        return false;
                    }
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-group-perm", false, new String[]{"<oermission>", args[2]}, new String[]{"<group>", args[0]}));
                    group.removePermission(args[2]);
                    return false;
                case "check":
                    if (!sender.hasPermission("gearz.permissions.group.check")) return false;
                    if (args.length != 3) return false;
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
                    if (!sender.hasPermission("gearz.permissions.group.show")) return false;
                    if (args.length != 2) return false;
                    for (String perm : group.getPermissions()) {
                        String[] split = perm.split(",");
                        sender.sendMessage(split[0] + " : " + split[1]);
                    }
                    return false;
                case "prefix":
                    if (!sender.hasPermission("gearz.permissions.group.prefix")) return false;
                    if (args.length < 3) return false;
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", group.getName()}));
                        group.prefix = null;
                        group.save();
                        return false;
                    }
                    String prefix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length);
                    group.prefix = prefix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
                    group.save();
                    return false;
                case "suffix":
                    if (!sender.hasPermission("gearz.permissions.group.suffix")) return false;
                    if (args.length < 3) return false;
                    if (args[2].trim().equals("null")) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", group.getName()}));
                        group.suffix = null;
                        group.save();
                        return false;
                    }
                    String suffix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length);
                    group.suffix = suffix;
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
                    group.save();
                    return false;
                case "tabcolor":
                    if (!sender.hasPermission("gearz.permissions.group.tabcolor")) return false;
                    if (args.length != 3) return false;
                    group.tabColor = args[2];
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", args[2]}));
                    group.save();
                    return false;
                case "namecolor":
                    if (!sender.hasPermission("gearz.permissions.group.namecolor")) return false;
                    if (args.length != 3) return false;
                    group.nameColor = args[2];
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", args[2]}));
                    group.save();
                    return false;
                case "addinheritance":
                    if (!sender.hasPermission("gearz.permissions.group.inheritance")) return false;
                    if (args.length != 3) return false;
                    PermGroup toAdd = permsManager.getGroup(args[2]);
                    if (toAdd == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                        return false;
                    }
                    permsManager.addInheritance(group, toAdd);
                    return false;
                case "removeinheritance":
                    if (!sender.hasPermission("gearz.permissions.group.inheritance")) return false;
                    if (args.length != 3) return false;
                    PermGroup toRemove = permsManager.getGroup(args[2]);
                    if (toRemove == null) {
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                        return false;
                    }
                    permsManager.addInheritance(group, toRemove);
                    return false;
                case "setladder":
                    if (!sender.hasPermission("gearz.permissions.group.ladder")) return false;
                    if (args.length != 3) return false;
                    permsManager.setLadder(group, args[2]);
                    return false;
                default:
                    return false;
            }
            return false;
        }
        return false;
    }
}
