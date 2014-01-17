package net.tbnr.gearz.modules;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/14/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Hub implements TCommandHandler, Listener {

    private List<Server> hubServers;

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public static List<Server> getHubServers() {
        return ServerManager.getServersWithGame("lobby");
    }

    public ServerInfo getAHubServer() {
        if (hubServers.size() < 1) return null;
        int x = 0;
        List<Integer> attempts = new ArrayList<>();
        ServerInfo info = null;
        while (info == null && attempts.size() < hubServers.size()) {
            while (attempts.contains(x)) {
                x = GearzBungee.getRandom().nextInt(hubServers.size());
            }
            info = ProxyServer.getInstance().getServerInfo(hubServers.get(x).getBungee_name());
            attempts.add(x);
        }
        return info;
    }

    public static boolean isHubServer(ServerInfo info) {
        for (Server s : GearzBungee.getInstance().getHub().hubServers) {
            if (s.getBungee_name().equals(info.getName())) return true;
        }
        return false;
    }

    @TCommand(aliases = {"leave", "done", "back"}, usage = "/hub", senders = {TCommandSender.Player}, permission = "gearz.hub", name = "hub")
    @SuppressWarnings("unused")
    public TCommandStatus hubCommand(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (isHubServer(player.getServer().getInfo())) {
            player.sendMessage(GearzBungee.getInstance().getFormat("already-in-hub", true));
            return TCommandStatus.SUCCESSFUL;
        }
        player.connect(getAHubServer());
        sender.sendMessage(GearzBungee.getInstance().getFormat("send-to-hub", true));
        return TCommandStatus.SUCCESSFUL;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onKickEvent(ServerKickEvent event) {
        if (event.getPlayer().getServer() == null) return;
        if (isHubServer(event.getPlayer().getServer().getInfo())) return;
        event.setCancelServer(getAHubServer());
        event.setCancelled(true);
        event.getPlayer().sendMessage(GearzBungee.getInstance().getFormat("server-kick", true, true, new String[]{"<reason>", event.getKickReason()}));
    }

    @Data
    @RequiredArgsConstructor
    static class HubServerReloadTask implements Runnable {

        @NonNull private Hub hubManager;

        @Override
        public void run() {
            this.hubManager.hubServers = getHubServers();
        }
    }
}
