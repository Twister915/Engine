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

package net.cogz.permissions.bukkit;

import net.cogz.permissions.PermGroup;
import net.cogz.permissions.PermPlayer;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.netcommand.NetCommand;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Map;


/**
 * Commands to manage permisisons for
 * groups and players alike. Includes
 * commands to update every field that
 * a PermPlayer or PermGroup stores.
 *
 * <p>
 * Latest Change: UUID Changes
 * <p>
 *
 * @author Jake
 * @since Unknown
 */
public class PermissionsCommands implements TCommandHandler {

    private PermissionsManager permsManager;

    public PermissionsCommands() {
        // Get the perm manager current instance
        permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
    }

    @TCommand(
            name = "player",
            description = "Player management command.",
            usage = "/player <player> <args>",
            permission = "gearz.permissions.player",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus player(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 2) return TCommandStatus.FEW_ARGS;
        // Continue if args is 2 or bigger

        PermPlayer player;
        try {
            // Gets the offline version of the player ~ .trim() removes whitespace from the argument (Just in case)
            player = permsManager.getOfflinePlayer(args[0].trim());
        } catch (Exception ex) {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player"));
            return TCommandStatus.SUCCESSFUL;
        }

        // If player is null return null player
        if (player == null) {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player"));
            return TCommandStatus.SUCCESSFUL;
        }

        // Commands to follow
        switch (args[1]) {
            // If reset or delete is the command : delete the player
            case "reset":
            case "delete":
                return deletePlayer(sender, player);
            case "set":
                return setPlayerPerm(sender, player, args);
            case "remove":
            case "unset":
                return unsetPlayersGroup(sender, player, args);
            case "check":
                return checkPlayerPerms(sender, player, args);
            case "perms":
            case "show":
            case "permissions":
                return showPermissions(sender, player, args);
            case "setgroup":
                return setPlayerGroup(sender, player, args);
            case "removegroup":
                return removePlayerGroup(sender, player, args);
            case "prefix":
                return setPrefix(sender, player, args);
            case "suffix":
                return setSuffix(sender, player, args);
            case "tabcolor":
                return setTabColor(sender, player, args);
            case "namecolor":
                return setNameColor(sender, player, args);
            default:
                return TCommandStatus.INVALID_ARGS;
        }
    }

    private TCommandStatus setNameColor(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.namecolor")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.FEW_ARGS;
        player.nameColor = args[2].trim();
        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", getChatColorFormatted(args[2])}));
        player.save();
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus setTabColor(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.tabcolor")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.FEW_ARGS;
        player.tabColor = args[2].trim();
        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", getChatColorFormatted(args[2])}));
        player.save();
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus setSuffix(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.suffix")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.FEW_ARGS;
        if (args[2].trim().equals("null")) {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", player.getName()}));
            player.suffix = null;
            player.save();
        } else {
            String suffix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
            player.suffix = suffix;
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
            player.save();
        }
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus setPrefix(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.prefix")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.FEW_ARGS;
        if (args[2].trim().equals("null")) {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", player.getName()}));
            player.prefix = null;
            player.save();
        } else {
            String prefix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
            player.prefix = prefix;
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
            player.save();
        }
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus removePlayerGroup(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.removegroup")) return TCommandStatus.PERMISSIONS;
        if (args.length < 2) return TCommandStatus.FEW_ARGS;
        PermGroup grp = permsManager.getGroup(player.getGroup());
        if (grp == null) {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
        } else {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
            player.removeGroup();
        }
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus setPlayerGroup(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.addgroup")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.FEW_ARGS;
        PermGroup group = permsManager.getGroup(args[2]);
        if (group == null) {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
        } else {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.added-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
            player.setGroup(group);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus showPermissions(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.show")) return TCommandStatus.PERMISSIONS;
        if (args.length < 2) return TCommandStatus.FEW_ARGS;
        String[] split;
        for (String perm : player.getPermissions()) {
            split = perm.split(",");
            sender.sendMessage(split[0] + " : " + split[1]);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus checkPlayerPerms(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.check")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.INVALID_ARGS;

        if (player.hasPermission(args[2])) {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-player-perm-value", false, new String[]{"<player>", args[0]}, new String[]{"<permission>", args[2]}, new String[]{"<value>", true + ""}));
        } else {
            sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-player-perm-invalid", false, new String[]{"<player>", args[0]}, new String[]{"<permission>", args[2]}));
        }
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus unsetPlayersGroup(CommandSender sender, PermPlayer player, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.remove")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.FEW_ARGS;

        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-perm", false, new String[]{"<permission>", args[2]}, new String[]{"<player>", args[0]}));
        player.removePermission(args[2]);
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus setPlayerPerm(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gearz.permissions.player.set")) return TCommandStatus.PERMISSIONS;
        if (args.length < 3) return TCommandStatus.FEW_ARGS;
        Boolean value = true;

        try {
            if (args.length >= 4) value = Boolean.parseBoolean(args[3]);
        } catch (Exception exception) {
            return TCommandStatus.INVALID_ARGS;
        }

        String permission = args[2];
        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-player-perm", false, new String[]{"<permission>", permission}, new String[]{"<player>", args[0]}, new String[]{"<value>", value + ""}));
        permsManager.givePermToPlayer(args[0].trim(), permission, value);
        return TCommandStatus.SUCCESSFUL;
    }

    private TCommandStatus deletePlayer(CommandSender sender, PermPlayer player) {
        if (!sender.hasPermission("gearz.permissions.player.delete")) return TCommandStatus.PERMISSIONS;
        String name = player.getName();
        player.remove();
        permsManager.onJoin(name);
        sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.player-deleted"));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "group",
            description = "Group management command.",
            usage = "/group",
            permission = "gearz.permissions.group",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus group(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length == 0) return TCommandStatus.FEW_ARGS;
        PermGroup group = permsManager.getGroup(args[0]);
        switch (args[1]) {
            case "create":
                if (!sender.hasPermission("gearz.permissions.group.create")) return TCommandStatus.PERMISSIONS;
                if (permsManager.getGroup(args[0]) != null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.duplicate-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (args.length < 2 || args.length > 3) return TCommandStatus.INVALID_ARGS;
                boolean defau = false;
                if (args.length == 3) {
                    defau = Boolean.parseBoolean(args[2]);
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.created-group", false, new String[]{"<group>", args[0]}, new String[]{"<default>", defau + ""}));
                permsManager.createGroup(args[0], defau);
                return TCommandStatus.SUCCESSFUL;
            case "delete":
                if (!sender.hasPermission("gearz.permissions.group.delete")) return TCommandStatus.PERMISSIONS;
                if (permsManager.getGroup(args[0]) == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.deleted-group", false, new String[]{"<group>", args[0]}));
                permsManager.deleteGroup(args[0]);
                return TCommandStatus.SUCCESSFUL;
            case "set":
                if (!sender.hasPermission("gearz.permissions.group.set")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3 || args.length > 4) return TCommandStatus.INVALID_ARGS;
                if (group == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
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
                if (!sender.hasPermission("gearz.permissions.group.remove")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                if (group == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-group-perm", false, new String[]{"<oermission>", args[2]}, new String[]{"<group>", args[0]}));
                permsManager.removeGroupPerm(group, args[2]);
                return TCommandStatus.SUCCESSFUL;
            case "check":
                if (!sender.hasPermission("gearz.permissions.group.check")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                if (group == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (group.hasPermission(args[2])) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-group-perm-value", false, new String[]{"<group>", args[0]}, new String[]{"<permission>", args[2]}, new String[]{"<value>", true + ""}));
                } else {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-group-perm-invalid", false, new String[]{"<group>", args[0]}, new String[]{"<permission>", args[2]}));
                }
                return TCommandStatus.SUCCESSFUL;
            case "perms":
            case "show":
            case "permissions":
                if (!sender.hasPermission("gearz.permissions.group.show")) return TCommandStatus.PERMISSIONS;
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                for (String perm : group.getPermissions()) {
                    String[] split = perm.split(",");
                    sender.sendMessage(split[0] + " : " + split[1]);
                }
                return TCommandStatus.SUCCESSFUL;
            case "prefix":
                if (!sender.hasPermission("gearz.permissions.group.prefix")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3) return TCommandStatus.INVALID_ARGS;
                if (args[2].trim().equals("null")) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", group.getName()}));
                    group.prefix = null;
                    group.save();
                    return TCommandStatus.SUCCESSFUL;
                }
                String prefix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
                group.prefix = prefix;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "suffix":
                if (!sender.hasPermission("gearz.permissions.group.suffix")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3) return TCommandStatus.INVALID_ARGS;
                if (args[2].trim().equals("null")) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", group.getName()}));
                    group.suffix = null;
                    group.save();
                    return TCommandStatus.SUCCESSFUL;
                }
                String suffix = GearzBukkitPermissions.getInstance().compile(args, 2, args.length).trim();
                group.suffix = suffix;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "tabcolor":
                if (!sender.hasPermission("gearz.permissions.group.tabcolor")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                group.tabColor = args[2].trim();
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", getChatColorFormatted(args[2])}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "namecolor":
                if (!sender.hasPermission("gearz.permissions.group.namecolor")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                group.nameColor = args[2].trim();
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", getChatColorFormatted(args[2])}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "addinheritance":
                if (!sender.hasPermission("gearz.permissions.group.inheritance")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                PermGroup toAdd = permsManager.getGroup(args[2]);
                if (toAdd == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                    return TCommandStatus.SUCCESSFUL;
                }
                permsManager.addInheritance(group, toAdd);
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "removeinheritance":
                if (!sender.hasPermission("gearz.permissions.group.inheritance")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                PermGroup toRemove = permsManager.getGroup(args[2]);
                if (toRemove == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                    return TCommandStatus.SUCCESSFUL;
                }
                permsManager.addInheritance(group, toRemove);
                group.save();
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "permissions",
            description = "Permissions command.",
            usage = "/permissions <args>",
            permission = "gearz.permissions",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus permissions(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) return TCommandStatus.FEW_ARGS;
        if (!sender.hasPermission("gearz.permissions")) return TCommandStatus.PERMISSIONS;
        switch (args[0]) {
            case "reload":
            case "refresh":
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.reload"));
                GearzBukkitPermissions.getInstance().getPermsManager().reload();
                NetCommand.withName("permissions").withArg("reload", true).send();
                return TCommandStatus.SUCCESSFUL;
            case "groups":
                sender.sendMessage(ChatColor.GOLD + "Permissions Groups:");
                for (Map.Entry<String, PermGroup> group : GearzBukkitPermissions.getInstance().getPermsManager().getGroups().entrySet()) {
                    sender.sendMessage(group.getKey());
                }
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
    }

    protected String getChatColorFormatted(String originalColor) {
        String colorCode = originalColor.replaceAll("\u0026", "");
        ChatColor chatColor = ChatColor.getByChar(colorCode);
        return chatColor + chatColor.name().replaceAll("_", " ").toLowerCase();
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.handleCommandStatus(status, sender);
    }
}
