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

package net.tbnr.gearz.game.single;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.NonNull;
import net.lingala.zip4j.exception.ZipException;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzException;
import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.arena.Arena;
import net.tbnr.gearz.arena.ArenaManager;
import net.tbnr.gearz.event.player.PlayerPriorityDetermineEvent;
import net.tbnr.gearz.game.*;
import net.tbnr.gearz.game.classes.GearzAbstractClass;
import net.tbnr.gearz.game.voting.AutoBoosts;
import net.tbnr.gearz.game.voting.Votable;
import net.tbnr.gearz.game.voting.VotingHandler;
import net.tbnr.gearz.game.voting.VotingSession;
import net.tbnr.gearz.network.GearzPlayerProvider;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerDisconnectEvent;
import net.tbnr.util.player.TPlayerJoinEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 */
public final class GameManagerSingleGame<PlayerType extends GearzPlayer, AbstractClassType extends GearzAbstractClass<PlayerType>> implements GameManager<PlayerType, AbstractClassType>, Listener, VotingHandler, TCommandHandler {
    private final Class<? extends GearzGame<PlayerType, AbstractClassType>> gearzGameClass;
    private GameLobby gameLobby;
    @Getter private GameMeta gameMeta;
    @Getter private GearzPlugin<PlayerType, AbstractClassType> plugin;
    private VotingSession votingSession;
    @Getter private GearzGame<PlayerType, AbstractClassType> runningGame;
    @Getter private final GearzPlayerProvider<PlayerType> playerProvider;
    private List<String> priorities = new ArrayList<>();
    private List<GameManagerConnector<PlayerType, AbstractClassType>> gameManagerConnectors = new ArrayList<>();

    public GameManagerSingleGame(Class<? extends GearzGame<PlayerType, AbstractClassType>> gameClass, GearzPlugin<PlayerType, AbstractClassType> plugin, GearzPlayerProvider<PlayerType> playerProvider) throws GearzException {
        this.gearzGameClass = gameClass;
        this.playerProvider = playerProvider;
        GameMeta gameMeta1 = gameClass.getAnnotation(GameMeta.class);
        if (gameMeta1 == null) {
            throw new GearzException("Invalid Game Class");
        }
        this.gameMeta = gameMeta1;
        Arena arena = ArenaManager.arenaFromDBObject(GameLobby.class, Gearz.getInstance().getMongoDB().getCollection("game_lobbys_v2").findOne(new BasicDBObject("game", gameMeta.key())));
        if (arena == null) throw new GearzException("No lobby found!");
        this.gameLobby = (GameLobby) arena;
        this.plugin = plugin;
        try {
            this.gameLobby.loadWorld();
        } catch (ZipException | IOException e) {
            e.printStackTrace();
        }
        populatePrioritiesList();
        load();
    }

    private void load() {
        Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
            @Override
            public void run() {
                ServerManager.setStatusString("load_lobby");
                ServerManager.setMaximumPlayers(gameMeta.maxPlayers());
                ServerManager.setOpenForJoining(false);
            }
        }, 2L);
        Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
            @Override
            public void run() {
                ServerManager.setStatusString("lobby");
                ServerManager.setOpenForJoining(true);
            }
        }, 5L);
        this.votingSession = new VotingSession(new ArrayList<GearzPlayer>(), plugin.getArenaManager().getRandomArenas(5), this, this);
        this.votingSession.startSession(60);
        ConfigurationSection configurationSection = Gearz.getInstance().getConfig().getConfigurationSection("vote-boosts");
        HashMap<String, Integer> integerHashMap = new HashMap<>();
        for (String s : configurationSection.getKeys(false)) {
            integerHashMap.put(s, configurationSection.getInt(s));
        }
        getPlugin().registerEvents(new AutoBoosts(integerHashMap));
    }

    @TCommand(
            usage = "/map",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "",
            name = "map")
    @SuppressWarnings("unused")
    public TCommandStatus mapCommand(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(meta.usage());
            return TCommandStatus.INVALID_ARGS;
        }

        if (this.runningGame == null) {
            sender.sendMessage(Gearz.getInstance().getFormat("game-strings.not-running", false));
            return TCommandStatus.SUCCESSFUL;
        }
        List<String> toBox = new ArrayList<>();
        toBox.add(format("game-strings.map-lore-author", this.getGameMeta(), new String[]{"<author>", this.runningGame.getArena().getAuthors()}));
        toBox.add(format("game-strings.map-lore-description", this.getGameMeta(), new String[]{"<description>", this.runningGame.getArena().getDescription()}));

        List<String> toSend = Gearz.getInstance().boxMessage(ChatColor.DARK_BLUE, toBox);
        sender.sendMessage(format("game-strings.map-title", this.getGameMeta(), new String[]{"<name>", this.runningGame.getArena().getName()}));
        for (String msg : toSend) {
            sender.sendMessage(msg);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    /**
     * Formats a String for this voting object
     *
     * @param s       The String to format
     * @param meta    The meta of the game (from the game manager)
     * @param strings Any passed formatters
     * @return The formatted text.
     */
    private String format(String s, GameMeta meta, String[]... strings) {
        return GearzGame.formatUsingMeta(meta, Gearz.getInstance().getFormat(s, true, strings));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        if (Bukkit.getOnlinePlayers().length < this.gameMeta.maxPlayers()) return;
        if (this.runningGame != null && this.runningGame.isRunning()) {
            event.setResult(PlayerLoginEvent.Result.KICK_FULL);
            return;
        }
        PlayerType personToKick = candidateForKicking(event.getPlayer());
        if (personToKick != null) {
            personToKick.getPlayer().kickPlayer(Gearz.getInstance().getFormat("formats.game-kick-premium"));
        } else {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Gearz.getInstance().getFormat("formats.game-full"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final TPlayerJoinEvent event) {
        TPlayer player1 = event.getPlayer();
        ServerManager.setPlayersOnline(Bukkit.getOnlinePlayers().length);
        ServerManager.addPlayer(player1.getPlayerName());
        player1.resetPlayer();
        final PlayerType gearzPlayer = playerProvider.getPlayerFromTPlayer(player1);
        event.setJoinMessage(Gearz.getInstance().getFormat("formats.join-message", false, new String[]{"<game>", this.gameMeta.shortName()}, new String[]{"<player>", player1.getPlayer().getDisplayName()}));
        spawn(gearzPlayer);
        if (this.runningGame == null) {
            this.votingSession.addPlayer(gearzPlayer);
        } else {
            this.runningGame.addPlayer(gearzPlayer);
            if (this.runningGame.isHideStream()) event.setJoinMessage(null);
        }
        for (GameManagerConnector<PlayerType, AbstractClassType> gameManagerConnector : this.gameManagerConnectors) {
            try {
                gameManagerConnector.playerConnectedToLobby(playerProvider.getPlayerFromTPlayer(player1), this);
            } catch (Throwable ignored) {
                //ignore
            }
        }
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) stack.getItemMeta();
        bookMeta.setAuthor(Gearz.getInstance().getFormat("formats.h2p-book-author"));
        bookMeta.setTitle(Gearz.getInstance().getFormat("formats.h2p-book-title", true, new String[]{"<game>", gameMeta.longName()}));
        int x = 0;
        while (gameMeta.description().length() > x) {
            int y = x + 256;
            if (gameMeta.description().length() < y) {
                y = gameMeta.description().length();
            }
            String substring = gameMeta.description().substring(x, y);
            bookMeta.addPage(substring);
            x += 256;
        }
        stack.setItemMeta(bookMeta);
        gearzPlayer.getPlayer().getInventory().setItem(7, stack);
        Gearz.getInstance().debug("GEARZ DEBUG ---<GameManagerSingleGame|156>--------< TPlayerJoinEvent has been CAUGHT for: " + gearzPlayer.toString());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onLeave(TPlayerDisconnectEvent event) {
        ServerManager.setPlayersOnline(Bukkit.getOnlinePlayers().length - 1);
        ServerManager.removePlayer(event.getPlayer().getPlayerName());
        event.setQuitMessage(Gearz.getInstance().getFormat("formats.leave-message", false, new String[]{"<game>", this.gameMeta.shortName()}, new String[]{"<player>", event.getPlayer().getPlayer().getDisplayName()}));
        PlayerType player = playerProvider.getPlayerFromTPlayer(event.getPlayer());
        if (this.runningGame != null) {
            this.runningGame.playerLeft(player);
            if (this.runningGame.isHideStream()) event.setQuitMessage(null);
        } else {
            if (this.votingSession.isVoting()) {
                votingSession.removePlayer(player);
            }
            if (Bukkit.getOnlinePlayers().length < getGameMeta().maxPlayers()) {
                ServerManager.setOpenForJoining(true);
            }
        }
        Gearz.getInstance().debug("GEARZ DEBUG ---<GameManagerSingleGame|155>--------< TPlayerDisconnectEvent has been CAUGHT for: " + player.toString());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(this.gameLobby.pointToLocation(this.gameLobby.spawnPoints.next()));
    }

    @Override
    public void beginGame(Integer id) throws GameStartException {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    public void beginGame(Integer id, Arena arena) throws GameStartException {
        this.votingSession.endSession();
        List<PlayerType> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerType player = playerProvider.getPlayerFromPlayer(p);
            Gearz.getInstance().debug("GEARZ DEBUG ---<GameManagerSingleGame|183>--------< beginGame / player loop has been CAUGHT for: " + player.toString());
            players.add(player);
        }
        GearzGame<PlayerType, AbstractClassType> game;
        try {
            game = gearzGameClass.getConstructor(List.class, Arena.class, GearzPlugin.class, GameMeta.class, Integer.class, GearzPlayerProvider.class).newInstance(players, arena, this.plugin, this.gameMeta, 0, playerProvider);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        this.runningGame = game;
        String format = Gearz.getInstance().getFormat("formats.map-loading", false, new String[]{"<map>", arena.getName()});
        for (PlayerType p : game.allPlayers()) {
            p.getTPlayer().sendMessage(format);
            p.getPlayer().playNote(p.getPlayer().getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
        }
        ServerManager.setOpenForJoining(false);
        ServerManager.setStatusString("load-map");
        try {
            arena.loadWorld();
        } catch (GearzException | IOException | ZipException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }
        String format2 = Gearz.getInstance().getFormat("formats.map-loaded", false, new String[]{"<map>", runningGame.getArena().getName()});
        for (PlayerType p : runningGame.allPlayers()) {
            if (p.getPlayer() == null) {
                continue;
            }
            p.getTPlayer().sendMessage(format2);
            p.getPlayer().playNote(p.getPlayer().getLocation(), Instrument.PIANO, Note.flat(1, Note.Tone.E));
        }
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.registerEvents(runningGame);
                if (runningGame != null) {
                    runningGame.startGame();
                }
                ServerManager.setOpenForJoining(true);
                ServerManager.setStatusString("spectate");
                Gearz.getInstance().debug("------> STARTING GEARZ GAME <-----");
                Gearz.getInstance().debug(runningGame.toString());
            }
        }, 40L);
    }

    @Override
    public void gameEnded(GearzGame game) {
        ServerManager.setOpenForJoining(false);
        ServerManager.setStatusString("game-over");
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.kickPlayer(Gearz.getInstance().getFormat("formats.game-kick"));
                }
                Bukkit.shutdown();
            }
        }, 200);
    }

    @Override
    public void spawn(PlayerType player) {
        player.getTPlayer().teleport(this.gameLobby.pointToLocation(this.gameLobby.spawnPoints.next()));
    }

    @Override
    public void onVotingDone(Map<Votable, Integer> data, VotingSession ses) {
        onVotingDone(data, ses, false);
    }

    public void onVotingDone(Map<Votable, Integer> data, VotingSession ses, boolean override) {
        if (ses == null) {
            return;
        }
        int length = Bukkit.getOnlinePlayers().length;
        if (!(this.gameMeta.maxPlayers() >= length && length >= this.gameMeta.minPlayers()) && !override) {
            Bukkit.broadcastMessage(GearzGame.formatUsingMeta(this.gameMeta, Gearz.getInstance().getFormat("game-strings.not-enough-players", true, new String[]{"<num>", String.valueOf(this.gameMeta.minPlayers() - length)})));
            ses.extendSession(60);
            return;
        }
        int currentMax = 0;
        Arena highestVotes = null;
        for (Map.Entry<Votable, Integer> integerArenaEntry : data.entrySet()) {
            if (!(integerArenaEntry.getKey() instanceof Arena)) {
                continue;
            }
            Arena a = (Arena) integerArenaEntry.getKey();
            this.plugin.getArenaManager().logVotes(a, integerArenaEntry.getValue());
            if (integerArenaEntry.getValue() <= currentMax) {
                continue;
            }
            highestVotes = (Arena) integerArenaEntry.getKey();
            currentMax = integerArenaEntry.getValue();
        }
        if (highestVotes == null) {
            highestVotes = this.plugin.getArenaManager().getArenas().get(Gearz.getRandom().nextInt(this.plugin.getArenaManager().getArenas().size()));
        }
        try {
            this.beginGame(0, highestVotes);
        } catch (GameStartException e) {
            e.printStackTrace();
            ses.extendSession(60);
        }
    }

    @Override
    public void disable() {
        this.gameLobby.unloadWorld();
        if (this.runningGame != null && this.runningGame.isRunning()) {
            this.runningGame.disable();
            this.runningGame.getArena().unloadWorld();
        }
    }

    @Override
    public void registerListener(GameManagerConnector<PlayerType, AbstractClassType> connector) {
        this.gameManagerConnectors.add(connector);
    }

    @Override
    public void removeListener(GameManagerConnector<PlayerType, AbstractClassType> connector) {
        this.gameManagerConnectors.remove(connector);
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) {
            return;
        }
        event.getEntity().remove();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) {
            return;
        }
        if (event.getPlayer().getLocation().getY() < 0) {
            spawn(playerProvider.getPlayerFromPlayer(event.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) {
            return;
        }
        event.getItem().remove();
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (this.runningGame != null && this.runningGame.isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) event.setCancelled(true);
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.handleCommandStatus(status, sender);
    }

    /**
     * Get the person on the server with lower priority then them if no player lower it returns null
     *
     * @return GearzPlayer ~ player with lower priority then them
     */
    private PlayerType candidateForKicking(@NonNull Player p) {
        PlayerType candidate = null;
        ArrayList<Player> players = new ArrayList<>();
        Collections.addAll(players, Bukkit.getOnlinePlayers());
        Integer integer = priorityForPlayer(p);
        PlayerPriorityDetermineEvent event = new PlayerPriorityDetermineEvent(getPlayerProvider().getPlayerFromPlayer(p));
        event = Gearz.getInstance().callEvent(event);
        if (event.isCancelled()) return null;
        for (int i = players.size() - 1; i >= 0; i--) {
            Player wannaBe = players.get(i);
            if (p.getName().equals(wannaBe.getName())) continue;
            if (integer < priorityForPlayer(wannaBe.getPlayer()) || event.isAbsolutePriority()) {
                candidate = playerProvider.getPlayerFromPlayer(p);
                break;
            }
        }
        return candidate;
    }

    /**
     * Get's priority of a player
     *
     * @param p Player to test priority for.
     * @return priority of player, -1 default
     */
    @NonNull
    private Integer priorityForPlayer(Player p) {
        Integer priority = priorities.size();
        String permissionPriority;
        Player player = p.getPlayer();
        for (int x = 0, l = priorities.size(); x < l; x++) {
            permissionPriority = "gearz.priority." + priorities.get(x);
            if (player.hasPermission(permissionPriority)) {
                priority = x;
            }
        }
        return priority;
    }

    public void populatePrioritiesList() {
        this.priorities = Gearz.getInstance().getConfig().getStringList("priorities");
    }

    @Override
    public boolean isIngame() {
        return runningGame != null;
    }
}
