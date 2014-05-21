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

package net.cogzmc.engine.gearz.modules;

import lombok.*;
import net.cogzmc.engine.gearz.GearzBungee;
import net.cogzmc.engine.gearz.player.bungee.GearzPlayerManager;
import net.cogzmc.engine.server.Server;
import net.cogzmc.engine.server.ServerManager;
import net.cogzmc.engine.util.bungee.command.TCommand;
import net.cogzmc.engine.util.bungee.command.TCommandHandler;
import net.cogzmc.engine.util.bungee.command.TCommandSender;
import net.cogzmc.engine.util.bungee.command.TCommandStatus;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Module that allows players to cycle
 * between the minigames provided on
 * a network.
 *
 * <p>
 * Latest Change: Create module
 * <p>
 *
 * @author Joey
 * @since Unknown
 */
public class ShuffleModule implements TCommandHandler, Listener {

    private final HashMap<ProxiedPlayer, ShuffleSession> shuffleSessionHashMap = new HashMap<>();

    @TCommand(name = "shuffle", aliases = {"sh"}, permission = "gearz.shuffle", senders = {TCommandSender.Player}, usage = "/shuffle")
    public TCommandStatus shuffle(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length > 0) {
            if (sender.hasPermission("gearz.shuffle.others")) {
                for (String arg : args) {
                    List<ProxiedPlayer> matchedPlayers = GearzPlayerManager.getInstance().getMatchedPlayers(arg);
                    for (ProxiedPlayer matchedPlayer : matchedPlayers) {
                        toggleShuffleFor(matchedPlayer);
                    }
                }
                return TCommandStatus.SUCCESSFUL;
            }
        }
        toggleShuffleFor((ProxiedPlayer) sender);
        return TCommandStatus.SUCCESSFUL;
    }

    public void startSessionFor(ProxiedPlayer player) {
        if (isShuffling(player)) return;
        ShuffleSession session = new ShuffleSession(player);
        session.start();
        GearzBungee.getInstance().registerEvents(session);
        this.shuffleSessionHashMap.put(player, session);
    }

    public void stopSessionFor(ProxiedPlayer player) {
        if (!isShuffling(player)) return;
        this.shuffleSessionHashMap.get(player).stop();
        this.shuffleSessionHashMap.remove(player);
    }

    public void toggleShuffleFor(ProxiedPlayer player) {
        if (!isShuffling(player)) startSessionFor(player);
        else stopSessionFor(player);
    }

    public boolean isShuffling(ProxiedPlayer player) {
        return this.shuffleSessionHashMap.containsKey(player);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        if (isShuffling(event.getPlayer())) stopSessionFor(event.getPlayer());
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    @Data
    @RequiredArgsConstructor
    public static class ShuffleSession implements Listener {
        @NonNull
        private final ProxiedPlayer player;
        private Server nextServer = null;
        private List<Server> serversPlayed;
        private boolean playingCurrently = false;
        private boolean started = false;
        private Integer attemptsToFindServer = 0;
        private List<String> attemptedGames;
        private static final Integer secondDelay = 5;
        private ScheduledTask task;

        public void start() {
            if (this.started) throw new IllegalStateException("This session has already started!");
            this.started = true;
            serversPlayed = new ArrayList<>();
            attemptedGames = new ArrayList<>();
            if (!HubModule.isHubServer(player.getServer().getInfo())) {
                playingCurrently = true;
                sendMessage(GearzBungee.getInstance().getFormat("shuffle-wait-for-hub", false));
            } else prepareForNextServer();
            sendMessage(GearzBungee.getInstance().getFormat("shuffle-enable", false, false));
        }

        public void stop() {
            this.started = false;
            sendMessage(GearzBungee.getInstance().getFormat("shuffle-disable", false, false));
            playerDisconnected();
        }

        public void sendMessage(String message) {
            player.sendMessage(message);
        }

        void connectToNextServer() {
            if (this.nextServer == null) throw new IllegalStateException("No next server defined!");
            if (this.playingCurrently) throw new IllegalStateException("Player is already ingame!");
            if (!this.started) throw new IllegalStateException("This session is not active!");
            ServerInfo info = ProxyServer.getInstance().getServerInfo(nextServer.getBungee_name());
            if (info == null) throw new IllegalStateException("Invalid next server defined");
            player.connect(info);
            sendMessage(GearzBungee.getInstance().getFormat("shuffle-connect", false, false, new String[]{"<game>", nextServer.getGame()}));
            this.task = null;
            serversPlayed.add(nextServer);
            this.nextServer = null;
            playingCurrently = true;
        }

        private void prepareForNextServer() throws IllegalStateException {
            try {
                if (this.playingCurrently) throw new IllegalStateException("Already ingame!");
                if (!HubModule.isHubServer(this.player.getServer().getInfo()))
                    throw new IllegalStateException("Not currently on Hub.");
                this.nextServer = getRandomServer();
                this.task = ProxyServer.getInstance().getScheduler().schedule(GearzBungee.getInstance(), new ShuffleStateChanger(this), secondDelay, TimeUnit.SECONDS);
                sendMessage(GearzBungee.getInstance().getFormat("shuffle-nextgame", false, false, new String[]{"<game>", this.nextServer.getGame()}, new String[]{"<time>", String.valueOf(secondDelay)}));
                remindPlayerAboutDisable();
            } catch (IllegalStateException ex) {
                sendMessage(GearzBungee.getInstance().getFormat("shuffle-error", false, false, new String[]{"<error>", ex.getMessage()}));
            }
        }

        void errorConnectingToServer() {
            this.task = null;
            prepareForNextServer();
        }

        void playerDisconnected() {
            if (this.task != null) {
                this.task.cancel();
            }
            this.started = false;
        }

        public void remindPlayerAboutDisable() {
            sendMessage(GearzBungee.getInstance().getFormat("shuffle-disable-reminder", false, false));
        }

        private Server getRandomServer() {
            String game = null;
            List<String> uniqueGames = ServerManager.getUniqueGames();
            int attempts = 0;
            while (attempts < 15 && (game == null ||
                    game.equals("lobby") ||
                    game.equals("watercooler") ||
                    (getLastPlayedServer() != null && getLastPlayedServer().getGame().equals(game)) ||
                    this.attemptedGames.contains(game))) {
                game = uniqueGames.get(GearzBungee.getRandom().nextInt(uniqueGames.size()));
                attempts++;
            }
            Server server = null;
            for (Server server1 : ServerManager.getServersWithGame(game)) {
                if (server1.getStatusString() == null) continue;
                if (!server1.getStatusString().equals("lobby")) continue;
                if (!server1.isCanJoin()) continue;
                if (server == null) {
                    server = server1;
                    continue;
                }
                if ((server1.getPlayerCount() == null ? 0 : server1.getPlayerCount()) > (server.getPlayerCount() == null ? 0 : server.getPlayerCount()))
                    server = server1;
            }
            if (server == null) {
                this.attemptedGames.add(game);
                attemptsToFindServer += 1;
                if (attemptsToFindServer >= 5)
                    throw new IllegalStateException("Unable to find an adequate server for you to join!");
                return getRandomServer();
            }
            this.attemptedGames = new ArrayList<>();
            attemptsToFindServer = 0;
            return server;
        }

        public Server getLastPlayedServer() {
            if (this.serversPlayed.size() < 1) return null;
            return this.serversPlayed.get(this.serversPlayed.size() - 1);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void serverSwitchEvent(ServerSwitchEvent event) {
            if (!event.getPlayer().equals(this.player)) return;
            if (!started) return;
            if (HubModule.isHubServer(event.getPlayer().getServer().getInfo())) {
                this.playingCurrently = false;
                prepareForNextServer();
                return;
            }
            if (playingCurrently) return;
            playingCurrently = true;
            sendMessage(GearzBungee.getInstance().getFormat("shuffle-wait-for-hub"));
            prepareForNextServer();
        }

    }

    @AllArgsConstructor
    @ToString
    public static class ShuffleStateChanger implements Runnable {

        @Getter @NonNull
        private final ShuffleSession shuffleSession;

        @Override
        public void run() {
            try {
                this.shuffleSession.connectToNextServer();
            } catch (IllegalStateException ex) {
                this.shuffleSession.errorConnectingToServer();
                this.shuffleSession.sendMessage(GearzBungee.getInstance().getFormat("shuffle-error", false, false, new String[]{"<error>", ex.getMessage()}));
            }
        }
    }
}
