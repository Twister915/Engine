package net.tbnr.gearz.friends;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.modules.PlayerInfoModule;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;
import net.tbnr.gearz.server.Server;
import net.tbnr.util.SimplePaginator;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.List;

/**
 * Created by Jake on 1/27/14.
 *
 * Purpose Of File: Handles the commands used for friends
 *
 * Latest Change:
 */
public class FriendCommands extends SimplePaginator implements TCommandHandler {
    public FriendCommands() {
        super(6);
    }

    @Override
    public String formatEntry(Object entry, int num, int optionNum) {
        if (optionNum == 1) {
            Friend friend = (Friend) entry;
            return formatPlayer(friend);
        } else if (optionNum == 2) {
            Friend friend = (Friend) entry;
            return formatRequest(friend);
        }
        return null;
    }

    /**
     * 1 == Friend Requests Header
     * 2 == Friends List Header
     */
    @Override
    public String formatHeader(int optionNum) {
        if (optionNum == 1) {
            return GearzBungee.getInstance().getFormat("friend-requests-header", false);
        } else if (optionNum == 2) {
            return GearzBungee.getInstance().getFormat("friend-header", false);
        }
        return null;
    }

    @TCommand(
            name = "friend",
            usage = "/friend <args...>",
            permission = "gearz.friend",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused, unchecked")
    public TCommandStatus friend(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("friend-usehelp", false));
            return TCommandStatus.SUCCESSFUL;
        }
        GearzPlayer gearzPlayer = GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender);
        ProxiedPlayer target = null;
        GearzPlayer targetGearzPlayer = null;
        if (args.length == 2) {
            target = ProxyServer.getInstance().getPlayer(args[1]);
            if (target != null) {
                targetGearzPlayer = GearzPlayerManager.getGearzPlayer(target);
            }
        }
        switch (args[0]) {
            case "add":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                if (target == null) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-null", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (target.getName().equals(sender.getName())) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-self", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                try {
                    gearzPlayer.addFriend(target.getName(), true);
                } catch (IllegalStateException e) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-already", false));
                    break;
                } catch (Friend.FriendRequestexception e) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-need-request", false));
                    try {
                        targetGearzPlayer.addRequest(sender.getName());
                    } catch (IllegalStateException ex) {
                        sender.sendMessage(GearzBungee.getInstance().getFormat("friend-already-request", false));
                        return TCommandStatus.SUCCESSFUL;
                    }
                    target.sendMessage(GearzBungee.getInstance().getFormat("friend-request-received", false, false, new String[]{"<player>", sender.getName()}));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBungee.getInstance().getFormat("friend-add", false, false, new String[]{"<player>", target.getName()}));
                target.sendMessage(GearzBungee.getInstance().getFormat("friend-add", false, false, new String[]{"<player>", sender.getName()}));
                break;
            case "remove":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                if (target == null) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-null"));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (target.getName().equals(sender.getName())) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-self", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                try {
                    gearzPlayer.removeFriend(target.getName(), true);
                } catch (IllegalStateException e) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-not"));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBungee.getInstance().getFormat("friend-remove", false, false, new String[]{"<player>", target.getName()}));
                target.sendMessage(GearzBungee.getInstance().getFormat("friend-remove-victim", false, false, new String[]{"<player>", sender.getName()}));
                break;
            case "deny":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                try {
                    gearzPlayer.removeRequest(args[1]);
                } catch (IllegalStateException e) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-no-request", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBungee.getInstance().getFormat("friend-deny", false, false, new String[]{"<player>", args[1]}));
                break;
            case "requests":
                if (args.length > 2) return TCommandStatus.INVALID_ARGS;
                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(GearzBungee.getInstance().getFormat("not-a-number", false));
                        return TCommandStatus.SUCCESSFUL;
                    }
                }
                try {
                    paginate(sender, gearzPlayer.getPendingRequests(), page, 1, 2);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("page-null", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                return TCommandStatus.SUCCESSFUL;
            case "help":
                sender.sendMessage(GearzBungee.getInstance().getFormat("friends-help-header", false));
                sender.sendMessage(formatHelp("/friend add <player>", "adds a player as a friend."));
                sender.sendMessage(formatHelp("/friend remove <player>", "removes a player as a friend"));
                sender.sendMessage(formatHelp("/friend join <player>", "joins the server that the specified friend is on"));
                sender.sendMessage(formatHelp("/friend deny <player>", "denys a player's friend request"));
                sender.sendMessage(formatHelp("/friend requests", "Lists all current friend requests"));
                sender.sendMessage(formatHelp("/friends", "lists all your friends"));
                return TCommandStatus.SUCCESSFUL;
            case "join":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                if (target == null) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-null", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                Server server = PlayerInfoModule.getServerForBungee(target.getServer().getInfo());
                if (((ProxiedPlayer) sender).getServer().equals(target.getServer())) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-same-server", false));
                } else if (server.isCanJoin()) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-join", false, false, new String[]{"<server>", PlayerInfoModule.getServerForBungee(target.getServer().getInfo()).getGame()}));
                    ((ProxiedPlayer) sender).connect(target.getServer().getInfo());
                } else {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("friend-join-bad", false));
                }
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "friends",
            aliases = {"fr", "f"},
            senders = {TCommandSender.Player},
            permission = "gearz.friends",
            usage = "/friends")
    @SuppressWarnings("unused")
    public TCommandStatus friends(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        GearzPlayer gearzPlayer = GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender);
        ProxiedPlayer player = (ProxiedPlayer) sender;
        int page = 1;
        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("not-a-number", false));
                return TCommandStatus.SUCCESSFUL;
            }
        }
        List<Friend> friends = gearzPlayer.getAllFriends();
        try {
            sendFriendsList(player, friends, page);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("page-null", false));
            return TCommandStatus.SUCCESSFUL;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @SuppressWarnings("unchecked")
    private void sendFriendsList(ProxiedPlayer player, List<Friend> friends, int page) throws IllegalArgumentException {
        paginate(player, friends, page, 2, 1);
    }

    private String formatPlayer(Friend friend) {
        if (friend.isOnline()) {
            ProxiedPlayer onlinePlayer = ProxyServer.getInstance().getPlayer(friend.getPlayer());
            return GearzBungee.getInstance().getFormat("friend-online", new String[]{"<player>", friend.getPlayer()}, new String[]{"<server>", PlayerInfoModule.getServerForBungee(onlinePlayer.getServer().getInfo()).getGame()});
        } else {
            return GearzBungee.getInstance().getFormat("friend-offline", new String[]{"<player>", friend.getPlayer()});
        }
    }

    private String formatRequest(Friend friend) {
        return GearzBungee.getInstance().getFormat("friend-request", new String[]{"<player>", friend.getPlayer()});
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public String formatHelp(String key, String value) {
        return GearzBungee.getInstance().getFormat("friends-help", false, false, new String[]{"<key>", key}, new String[]{"<value>", value});
    }
}
