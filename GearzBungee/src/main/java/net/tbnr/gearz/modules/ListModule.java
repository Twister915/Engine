package net.tbnr.gearz.modules;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * List module, deals with the /list and /where command.
 */
public class ListModule implements TCommandHandler, Listener {
    @Getter
    public Collection<ProxiedPlayer> staff = new ArrayList<>(); //Cache this for efficiency, don't for loop it every tie someone types /list staff lol

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(PostLoginEvent event) {
        if (event.getPlayer().hasPermission("gearz.staff")) {
            staff.add(event.getPlayer());
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDisconnect(PlayerDisconnectEvent event) {
        if (staff.contains(event.getPlayer())) staff.remove(event.getPlayer());
    }

    @TCommand(aliases = {"who", "w", "ls", "players", "online"}, usage = "/list", senders = {TCommandSender.Player, TCommandSender.Console}, permission = "gearz.list", name = "list")
    @SuppressWarnings("unused")
    public TCommandStatus list(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        List<String> multiMessage = new ArrayList<>();
        multiMessage.add(GearzBungee.getInstance().getFormat("list-online", false, false, new String[]{"<online>", String.valueOf(ProxyServer.getInstance().getOnlineCount())}, new String[]{"<max>", String.valueOf(GearzBungee.getInstance().getMaxPlayers())}));
        boolean shouldShowMoreMessage = false;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("more") || args[0].equalsIgnoreCase("all")) {
                multiMessage.add(formatPlayerList(ProxyServer.getInstance().getPlayers()));
            } else if (args[0].equalsIgnoreCase("staff")) {
                multiMessage.add(GearzBungee.getInstance().getFormat("list-server-title", false, false, new String[]{"<server>", "Staff"}, new String[]{"<online>", String.valueOf(staff.size())}) + formatPlayerList(staff));
            }
        } else {
            shouldShowMoreMessage = true;
        }
        if (shouldShowMoreMessage) {
            multiMessage.add(GearzBungee.getInstance().getFormat("list-more-server-message", false));
        }
        List<String> stringList = GearzBungee.boxMessage(ChatColor.BLUE, multiMessage);
        for (String s : stringList) {
            sender.sendMessage(s);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "where", permission = "gearz.where", senders = {TCommandSender.Console, TCommandSender.Player}, usage = "/where <name>", aliases = {"find", "search", "seen"})
    @SuppressWarnings("unused")
    public TCommandStatus where(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 1) return TCommandStatus.FEW_ARGS;
        List<String> messages = new ArrayList<>();
        for (String arg : args) {
            String name = arg;
            boolean online = false;
            String server = null;
            ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(arg);
            if (player1 == null) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.getName().toUpperCase().startsWith(arg.toUpperCase())) {
                        player1 = player;
                        break;
                    }
                }
            }
            if (player1 != null) {
                name = player1.getName();
                online = true;
                server = player1.getServer().getInfo().getName();
            }
            messages.add(GearzBungee.getInstance().getFormat("player-status-where", false, true, new String[]{"<status>", online ? "&aonline" : "&coffline"}, new String[]{"<name>", name}));
            if (online) {
                messages.add(GearzBungee.getInstance().getFormat("player-server-where", false, true, new String[]{"<server>", server}));
            }
        }
        for (String s : GearzBungee.boxMessage(ChatColor.BLUE, messages)) {
            sender.sendMessage(s);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    /**
     * Formats a list of players for a specific server.
     *
     * @param server The server to list the players of.
     * @return The formatted String.
     */
    private String getServer(ServerInfo server) {
        return GearzBungee.getInstance().getFormat("list-server-title", false, false, new String[]{"<server>", server.getName()}, new String[]{"<online>", String.valueOf(server.getPlayers().size())}) + formatPlayerList(server.getPlayers());
    }

    /**
     * Format a list player
     *
     * @param players The players to format mate.
     * @return The formatted list of players.
     */
    public String formatPlayerList(Collection<ProxiedPlayer> players) {
        int index = 0;
        StringBuilder builder = new StringBuilder();
        for (ProxiedPlayer player : players) {
            builder.append(GearzBungee.getInstance().getFormat("list-player", false, false, new String[]{"<name>", player.getName()}));
            if (index + 1 < players.size()) {
                builder.append(GearzBungee.getInstance().getFormat("list-commaspace", false)).append(" ");
            }
            if (index > 49) {
                builder.append(GearzBungee.getInstance().getFormat("list-more", false, false, new String[]{"<num>", String.valueOf((players.size() + 1) - index)}));
                break;
            }
            index++;
        }
        return builder.toString();
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
