/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.game;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.*;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.arena.Arena;
import net.tbnr.gearz.effects.EnderBar;
import net.tbnr.gearz.event.game.GameEndEvent;
import net.tbnr.gearz.event.game.GamePreStartEvent;
import net.tbnr.gearz.event.game.GameStartEvent;
import net.tbnr.gearz.event.player.*;
import net.tbnr.gearz.netcommand.BouncyUtils;
import net.tbnr.gearz.network.GearzPlayerProvider;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.BlockRepair;
import net.tbnr.util.RandomUtils;
import net.tbnr.util.ServerSelector;
import net.tbnr.util.inventory.InventoryGUI;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerStorable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GearzGame is a class to represent a game.
 */
@SuppressWarnings({"NullArgumentToVariableArgMethod", "DefaultFileTemplate", "UnusedDeclaration"})
@EqualsAndHashCode(of = {"id", "arena", "players"}, doNotUseGetters = true, callSuper = false)
@ToString(of = {"arena", "id", "running", "players", "spectators", "gameMeta"})
public abstract class  GearzGame<PlayerType extends GearzPlayer> extends GameDelegate<PlayerType> implements Listener {
    private final Arena arena;
    private final Set<PlayerType> players;
    private final Set<PlayerType> spectators;
    @Getter(AccessLevel.PROTECTED) private final Set<PlayerType> addedPlayers;
    @Getter(AccessLevel.PROTECTED) private final Set<PlayerType> endedPlayers;
    @Getter(AccessLevel.PROTECTED) private final InventoryGUI spectatorGui;
    private final GameMeta gameMeta;
    private final GearzPlugin<PlayerType> plugin;
    private final Integer id;
    private final GearzMetrics<PlayerType> metrics;
    @Getter(AccessLevel.PROTECTED) private final PvPTracker<PlayerType> tracker;
    @Getter(AccessLevel.PROTECTED) private final GearzPlayerProvider<PlayerType> playerProvider;
    @Getter private boolean running;
	@Getter private boolean hideStream;
    private final static ChatColor[] progressiveWinColors =
            {ChatColor.DARK_GREEN, ChatColor.GREEN,
                    ChatColor.DARK_AQUA, ChatColor.AQUA,
                    ChatColor.DARK_BLUE, ChatColor.BLUE,
                    ChatColor.DARK_RED, ChatColor.RED,
                    ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE,
                    ChatColor.DARK_GRAY, ChatColor.GRAY};
    private static enum NumberSuffixes {
        ONE('1', "st"),
        TWO('2', "nd"),
        THREE('3', "rd"),
        OTHER('*', "th");

        private final char numberCharacter;
        private final String suffix;

        NumberSuffixes(char numberCharacter, String suffix) {
            this.suffix = suffix;
            this.numberCharacter = numberCharacter;
        }
        public static NumberSuffixes valueOf(char chat) {
            for (NumberSuffixes numberSuffixes : NumberSuffixes.values()) {
                if (numberSuffixes.getChar() == chat) return numberSuffixes;
            }
            return NumberSuffixes.OTHER;
        }
        public char getChar() {
            return this.numberCharacter;
        }
        public String getSuffix() {
            return this.suffix;
        }
        public static NumberSuffixes getForString(String string) {
            return valueOf(string.charAt(string.length()-1));
        }
    }

    public enum Explosion {
        NORMAL,
        NO_BLOCK_DROP,
        NO_BLOCK_DAMAGE,
        NO_BLOCK_DAMAGE_AND_NO_DROP,
        REPAIR_BLOCK_DAMAGE,
        REPAIR_BLOCK_DAMAGE_AND_NO_DROP,
        NONE
    }

    /**
     * New game in this arena
     *
     * @param players The players in this game
     * @param arena   The Arena that the game is in.
     * @param plugin  The plugin that handles this Game.
     * @param meta    The meta of the game.
     */
    public GearzGame(List<PlayerType> players, Arena arena, GearzPlugin<PlayerType> plugin, GameMeta meta, Integer id, final GearzPlayerProvider<PlayerType> playerProvider) {
        this.playerProvider = playerProvider;
        this.arena = arena;
        /*this.players = players;
        this.spectators = new ArrayList<>();*/
        this.players = new HashSet<>();
        this.addedPlayers = new HashSet<>();
        for (PlayerType player : players) {
            if (player.isValid()) this.players.add(player);
        }

        for (PlayerType player : players) {
            Gearz.getInstance().debug("GEARZ DEBUG ---<GearzGame|73>--------< <init> / player loop has been CAUGHT for: " + player.getUsername());
        }
        this.tracker = new PvPTracker<>(this);
        this.spectators = new HashSet<>();
        this.endedPlayers = new HashSet<>();
        this.plugin = plugin;
        this.gameMeta = meta;
        this.id = id;
		this.hideStream = false;
        this.metrics = GearzMetrics.beginTracking(this);
        this.spectatorGui = new InventoryGUI(getPlayersForMenu(), ChatColor.RED + "Spectator menu.", new InventoryGUI.InventoryGUICallback() {
            @Override
            public void onItemSelect(InventoryGUI gui, InventoryGUI.InventoryGUIItem item, Player player) {
                Player target = Bukkit.getServer().getPlayer(item.getName());
                if (target == null) return;
                player.teleport(target.getLocation());
                player.closeInventory();
                player.sendMessage(getFormat("spectator-tp", new String[]{"<player>", target.getName()}));
                TPlayer tPlayer = resolvePlayer(player).getTPlayer();
                tPlayer.playSound(Sound.ENDERMAN_TELEPORT);
                tPlayer.playSound(Sound.ARROW_HIT);
            }

            @Override
            public void onGUIOpen(InventoryGUI gui, Player player) {
                gui.updateContents(getPlayersForMenu());
            }

            @Override
            public void onGUIClose(InventoryGUI gui, Player player) {
                // IGNORE
            }
        });
    }

    /**
     * Starts the game, called publicly to support required logic before telling the game itself that we're starting :D
     */
    public final void startGame() {
        //Required Logic stuff :D
        if (this.running) {
            return;
        }
        this.running = true;
        GamePreStartEvent gamePreStartEvent = new GamePreStartEvent(this);
        Bukkit.getPluginManager().callEvent(gamePreStartEvent);
        if (gamePreStartEvent.isCancelled()) {
            broadcast(getFormat("game-cancelled", new String[]{"<reason>", gamePreStartEvent.getReasonCancelled()}));
            return;
        }
        this.gamePreStart();
        HashSet<PlayerType> players1 = this.getPlayers();
        for (PlayerType player : players1) {
            Bukkit.getPluginManager().callEvent(new PlayerGameEnterEvent(this, player));
            makePlayer(player);
        }
        broadcast(getFormat("game-start"));
        this.metrics.startGame();
        try {
            this.gameStarting();
        } catch (Throwable t) {
            t.printStackTrace();
            for (OfflinePlayer p : Bukkit.getOperators()) {
                if (!p.isOnline()) {
                    continue;
                }
                PlayerType gearzPlayer = resolvePlayer(p.getPlayer());
                gearzPlayer.sendException(t);
            }
        }
        for (PlayerType player2 : players1) {
            try {
                Location location = playerRespawn(player2);
                player2.getTPlayer().teleport(location);
                activatePlayer(player2);
            } catch (Throwable t) {
                //player2.sendException(t);
            }
        }
        //Ghosting player fix hopefully
        List<Player> playerEntityList = getPlayerEntityList(players1);
        for (Player player : playerEntityList) {
            ProtocolLibrary.getProtocolManager().updateEntity(player, playerEntityList);
        }

        for (Entity e : this.arena.getWorld().getEntitiesByClasses(LivingEntity.class)) {
            if (e instanceof Player) {
                continue;
            }
            if (!allowEntitySpawn(e)) {
                e.remove();
            }
        }
        this.arena.cleanupDrops();
        this.tracker.startGame();
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
    }

    public final boolean isIngame(PlayerType player) {
        return this.allPlayers().contains(player);
    }


    protected final void stopGameForPlayer(PlayerType player, GameStopCause cause) {
        if (this.endedPlayers.contains(player)) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new PlayerGameLeaveEvent(player, this));
        player.getTPlayer().resetPlayer();
        onPlayerGameEnd(player, cause);
        this.endedPlayers.add(player);
    }

    private void stopGame(GameStopCause cause) {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.gameEnding();
        for (PlayerType player : allPlayers()) {
            stopGameForPlayer(player, cause);
        }
        broadcast(getFormat("game-ending"));
        this.metrics.finishGame();
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
        HandlerList.unregisterAll(this);
        this.plugin.getGameManager().gameEnded(this);
        this.tracker.saveKills();
    }

    private List<Player> getPlayerEntityList(HashSet<PlayerType> players) {
        ArrayList<Player> players1 = new ArrayList<>();
        for (PlayerType player : players) {
            players1.add(player.getPlayer());
        }
        return players1;
    }

    public final void disable() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.metrics.finishGame();
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
    }

    /**
     * Stop's the current game ~ forcefully
     */
    public final void stopGame() {
        stopGame(GameStopCause.FORCED);
    }

    /**
     * Stop the current game ~ normally on finish
     */
    protected final void finishGame() {
        this.finishGame(false);
    }

	/**
	 * Stop the current game ~ normally on finish
	 * @param hideStream whether or not to hide the join/leave stream
	 */
	protected final void finishGame(boolean hideStream) {
		this.hideStream = hideStream;
		stopGame(GameStopCause.GAME_END);
	}



    @EventHandler
    public final void onExplosion(EntityExplodeEvent event) {
        if (getExplosionType() == Explosion.NO_BLOCK_DAMAGE || getExplosionType() == Explosion.NO_BLOCK_DAMAGE_AND_NO_DROP || getExplosionType() == Explosion.NONE) {
            event.setCancelled(true);
        }

        if (getExplosionType() == Explosion.NO_BLOCK_DROP || getExplosionType() == Explosion.NO_BLOCK_DAMAGE_AND_NO_DROP || getExplosionType() == Explosion.REPAIR_BLOCK_DAMAGE_AND_NO_DROP || getExplosionType() == Explosion.NONE) {
            event.setYield(0F);
        }

        if (getExplosionType() == Explosion.REPAIR_BLOCK_DAMAGE || getExplosionType() == Explosion.REPAIR_BLOCK_DAMAGE_AND_NO_DROP) {
            final Location center = event.getLocation();
            final List<Block> oldBlocks = event.blockList();
            final List<BlockState> toRemove = new ArrayList<>();

            for (Block block : oldBlocks) {
                if (block.getType() != Material.TNT) {
                    toRemove.add(block.getState());
                }
            }

            BlockRepair.performRegen(toRemove, center, 16, 40L);
        }
    }

    @EventHandler
    public final void onLeafDecay(LeavesDecayEvent event) {
        if (!canLeafsDecay()) {
            event.setCancelled(true);
        }
    }

    /**
     * Get's the players which are playing the game
     *
     * @return List<GearzPlayer> ~ List of players
     */
    public final HashSet<PlayerType> getPlayers() {
        HashSet<PlayerType> players = new HashSet<>();
        players.addAll(this.players);
        return players;
    }

    /**
     * Get's current instance of game
     *
     * @return Arena ~ current instance of game
     */
    public final Arena getArena() {
        return this.arena;
    }

    /**
     * Get's the players which are spectating
     *
     * @return List<PlayerType> ~ List of Players
     */
    public final HashSet<PlayerType> getSpectators() {
        HashSet<PlayerType> spectators = new HashSet<>();
        for (PlayerType player : this.spectators) {
            if (player.getPlayer() != null) {
                spectators.add(player);
            }
        }
        return spectators;
    }

    public final void addPlayer(PlayerType player) {
        Gearz.getInstance().debug("GEARZ DEBUG ---<GearzGame|334>--------< addPlayer has been CAUGHT for: " + player.getUsername());
        this.addedPlayers.add(player);
        this.endedPlayers.add(player);
        makeSpectator(player);
    }

    /**
     * Check's whether a player is playing
     *
     * @param player ~ (in PlayerType wrapper) to be checked
     * @return boolean ~ true or false whether the player is playing
     */
    public final boolean isPlaying(PlayerType player) {
        return this.players.contains(player);
    }

    public final boolean isSpectating(PlayerType player) {
        return this.spectators.contains(player);
    }

    /**
     * Turn's a player into a spectator
     *
     * @param player ~ (in PlayerType wrapper) to become a spectator
     */
    protected final void makeSpectator(PlayerType player) {
        player.getTPlayer().resetPlayer();
        this.spectators.add(player);
        Bukkit.getPluginManager().callEvent(new PlayerBeginSpectateEvent(player, this));
        player.getTPlayer().sendMessage(getFormat("begin-spectating"));
        Player player2 = player.getPlayer();
        player2.setGameMode(GameMode.ADVENTURE);
        player2.setAllowFlight(true);
        player2.setFlying(true);
        hideFromAll(player);
        player.getTPlayer().playSound(Sound.FIZZ);
        if (isPlaying(player)) {
            this.players.remove(player);
        } else {
            try {
                player.getTPlayer().teleport(playerRespawn(player));
            } catch (Throwable t) {
                t.printStackTrace();
                player.sendException(t);
            }
        }
        for (PlayerType player1 : spectators) {
            Player player3 = player1.getPlayer();
            if (player3 == null) {
                continue;
            }
            if (!player3.isOnline()) {
                continue;
            }
            if (!player2.isOnline()) {
                continue;
            }
            player2.hidePlayer(player3);
        }
        onPlayerBecomeSpectator(player);
        player.getTPlayer().giveItem(Material.BOOK, 1, (short) 0, getFormat("spectator-chooser"));
        player.getTPlayer().giveItem(Material.BOOK, 1, (short) 0, getFormat("server-book"));

        spectatorGui.updateContents(getPlayersForMenu());
        RandomUtils.setPlayerCollision(player.getPlayer(), false);
    }

    protected final ArrayList<InventoryGUI.InventoryGUIItem> getPlayersForMenu() {
        ArrayList<InventoryGUI.InventoryGUIItem> items = new ArrayList<>();
        try {
            for (PlayerType player : getPlayers()) {
                Gearz.getInstance().debug("GEARZ DEBUG ---<GearzGame|399>--------< getPlayersForMenu / player loop has been CAUGHT for: " + player.toString());

                if (!player.isValid()) {
                    continue;
                }

                ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                ItemMeta itemMeta = stack.getItemMeta();
                String name = player.getPlayer().getName();
                itemMeta.setDisplayName(getGameMeta().mainColor() + name);
                /*try {
                    List<String> headImage = ImageToChatBukkitUtil.getHeadImage(name, true);
                    itemMeta.setLore(headImage);
                } catch (RuntimeException ex) {
                    System.err.println("Could not getSetting head for " + name);
                }*/
                stack.setItemMeta(itemMeta);
                //stack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
                items.add(new InventoryGUI.InventoryGUIItem(stack, player.getUsername()));
            }
        } catch (NullPointerException npe) {
            Gearz.getInstance().debug("GEARZ DEBUG ---<GearzGame|417>--------< getPlayersForMenu / player loop has thrown a npe: " + npe.getCause());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return items;
    }

    /**
     * Hide's a player from all other players in the game (inc spectators)
     *
     * @param player ~ (in PlayerType wrapper) to be hidden
     */
    private void hideFromAll(PlayerType player) {
        for (PlayerType player1 : allPlayers()) {
            if (player1.getPlayer() == null) {
                continue;
            }
            if (!player1.getPlayer().isOnline()) {
                continue;
            }
            if (player.getPlayer() == null) {
                continue;
            }
            if (!player.getPlayer().isOnline()) {
                continue;
            }
            player1.getPlayer().hidePlayer(player.getPlayer());
        }
    }

    private void showForAll(PlayerType player) {
        for (PlayerType player1 : allPlayers()) {
            if (player1.getPlayer() == null) {
                continue;
            }
            if (!player1.getPlayer().isOnline()) {
                continue;
            }
            if (player.getPlayer() == null) {
                continue;
            }
            if (!player.getPlayer().isOnline()) {
                continue;
            }
            player1.getPlayer().showPlayer(player.getPlayer());
        }
    }

    protected final void fakeDeath(final PlayerType player) {
        player.getTPlayer().resetPlayer();
        PlayerGameDeathEvent event = new PlayerGameDeathEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (!canPlayerRespawn(player)) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    makeSpectator(player);
                }
            }, 1);
            return;
        }
        player.getTPlayer().teleport(playerRespawn(player));
        player.getPlayer().playNote(player.getPlayer().getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
        final PlayerGameRespawnEvent respawnEvent = new PlayerGameRespawnEvent(player, this);
        Bukkit.getScheduler().runTaskLater(
                getPlugin(),
                new Runnable() {
                    @Override
                    public void run() {
                        activatePlayer(player);
                        Bukkit.getPluginManager().callEvent(respawnEvent);
                    }
                },
                1L
        );
    }

    protected final void makePlayer(PlayerType player) {
        if (this.endedPlayers.contains(player)) {
            throw new IllegalStateException("You cannot restart a game for a player whom is ended");
        }
        if (!this.players.contains(player)) {
            this.players.add(player);
        }
        if (this.spectators.contains(player)) {
            this.spectators.remove(player);
        }
        player.getTPlayer().resetPlayer();
        showForAll(player);
        onPlayerBecomePlayer(player);
        player.setGame(this);
    }

    /**
     * Removes a player from the game, depending on if there spectating or not it will give different GameStopCause
     *
     * @param player ~ (in PlayerType wrapper) to be removed
     */
    public final void removePlayer(PlayerType player) {
        if (!this.isRunning()) {
            return;
        }
        Gearz.getInstance().debug("GEARZ DEBUG ---<GearzGAme|483>--------< removePlayer has been CAUGHT for: " + player.getUsername());
        GameStopCause cause = isSpectating(player) ? GameStopCause.GAME_END : GameStopCause.FORCED;
        playerLeft(player);
        stopGameForPlayer(player, cause);
        plugin.getGameManager().spawn(player);
    }

    public final void playerLeft(PlayerType player) {
        Gearz.getInstance().debug("GEARZ DEBUG ---<GearzGAme|490>--------< playerLeft has been CAUGHT for: " + player.getUsername());
        this.players.remove(player);
        this.spectators.remove(player);
        //this.pendingPoints.remove(player);
        player.setGame(null);
        if (this.players.size() < 2) {
            stopGame(GameStopCause.FORCED);
        }
        removePlayerFromGame(player);
        RandomUtils.setPlayerCollision(player.getPlayer(), true);
    }

    public final String getFormatBase(String format) {
        return formatUsingMeta(this.gameMeta, Gearz.getInstance().getFormat("game-strings." + format));
    }

    public static String formatUsingMeta(GameMeta meta, String string) {
        String[][] array = {new String[]{"<p>", meta.mainColor().toString()}, new String[]{"<s>", meta.secondaryColor().toString()}, new String[]{"<gname>", meta.longName()}, new String[]{"<gn>", meta.shortName()}, new String[]{"<version>", meta.version()}, new String[]{"<description>", meta.description()}, new String[]{"<key>", meta.key()}, new String[]{"<max>", String.valueOf(meta.maxPlayers())}, new String[]{"<min>", String.valueOf(meta.minPlayers())}};
        String finalS = string;
        for (String[] element : array) {
            finalS = finalS.replaceAll(element[0], element[1]);
        }
        return (finalS == null ? string : finalS);
    }

    public final String getFormat(String format, String[]... args) {
        String formatBase = getFormatBase(format);
        if (args != null) {
            for (String[] arg : args) {
                if (arg.length < 2) {
                    continue;
                }
                formatBase = formatBase.replaceAll(arg[0], arg[1]);
            }
        }
        return getFormatBase("prefix") + formatBase;
    }

    public final String getFormat(String format) {
        return this.getFormat(format, null);
    }

    public final String getPluginFormat(String format, boolean prefix, String[]... args) {
        String format1 = plugin.getFormat(format);
        if (args != null) {
            for (String[] arg : args) {
                if (arg.length < 2) {
                    continue;
                }
                format1 = format1.replaceAll(arg[0], arg[1]);
            }
        }
        format1 = formatUsingMeta(this.gameMeta, format1);
        if (prefix) {
            format1 = getFormatBase("prefix") + format1;
        }
        return (format1 == null ? format : format1);
    }

    public final String getPluginFormat(String format) {
        return getPluginFormat(format, true);
    }

    public final String getPluginFormat(String format, boolean prefix) {
        return getPluginFormat(format, prefix, null);
    }

    /**
     * Broadcast's message to all players in the game
     *
     * @param message ~ The message to Broadcast
     */
    public final void broadcast(String message) {
        for (PlayerType player : allPlayers()) {
            try {
                player.getTPlayer().sendMessage(message);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Get's all the players including spectators
     *
     * @return List<PlayerType> List of players (in GearzPlayer wrapper) inc. Spectators
     */
    public final HashSet<PlayerType> allPlayers() {
        HashSet<PlayerType> allPlayers = new HashSet<>();
        allPlayers.addAll(this.getPlayers());
        allPlayers.addAll(this.getSpectators());
        return allPlayers;
    }

    /**
     * Get's the Plugin
     *
     * @return Plugin ~ the plugin instance
     */
    public final GearzPlugin getPlugin() {
        return this.plugin;

    }

    /**
     * Get's the id of the game
     *
     * @return Integer ~ Id of game
     */
    public final Integer getId() {
        return id;
    }

    /*
     * Spectator implementation
     * This code will prevent spectators from doing bad stuff.
     */
    @EventHandler
    public final void onPlayerInteract(PlayerInteractEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (!isIngame(player)) {
            return;
        }
        Block type = event.getClickedBlock();
        if (type != null && type.getType() == Material.BED_BLOCK) {
            event.setCancelled(true);
            return;
        }
        if (isSpectating(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.BOOK && event.getAction() != Action.PHYSICAL) {
                if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(getFormat("server-book"))) {
                    ServerSelector serverSelector = new ServerSelector(this.getGameMeta().key(), new ServerSelector.SelectorCallback() {
                        @Override
                        public void onItemSelect(ServerSelector selector, InventoryGUI.InventoryGUIItem item, Player player) {
                            /**
                             * The reason you need to test as the person could have the selector
                             * open for a while, and he clicks the last item while a server is restarting
                             * so the server is no longer online and therefore is not in the servers list
                             * Though the inventory is already open so it's not updated
                             * Therefore it causes and IndexOutOfBoundsException
                             * @see java.lang.IndexOutOfBoundsException
                             */
                            net.tbnr.gearz.server.Server server;
                            try {
                                server = selector.getServers().get(
                                        /** if */item.getSlot() > selector.getServers().size() ?
                                        /** true */0 : /** false */item.getSlot()
                                );
                            } catch (IndexOutOfBoundsException e) {
                                selector.update();
                                return;
                            }
                            BouncyUtils.sendPlayerToServer(player, server);
                        }

                        @Override
                        public void onSelectorOpen(ServerSelector selector, Player player) {

                        }

                        @Override
                        public void onSelectorClose(ServerSelector selector, Player player) {

                        }
                    });
                    serverSelector.open(player.getPlayer());
                    event.setCancelled(true);
                    return;
                }
                spectatorGui.open(player.getPlayer());
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            return;
        }
        if (event.getPlayer().getItemInHand().getType() == Material.BOW) {
            if (!canDrawBow(player)) {
                player.getTPlayer().sendMessage(getFormat("no-bow"));
                event.setCancelled(true);
            }
            return;
        }
        if (event.getPlayer().getItemInHand().getType() == Material.POTION || event.getPlayer().getItemInHand().getType() == Material.EXP_BOTTLE) {
            if (!canUsePotion(player)) {
                player.getTPlayer().sendMessage(getFormat("no-potion"));
                event.setCancelled(true);
            }
            return;
        }
        if (!canUse(player)) {
            if (event.getAction() != Action.PHYSICAL)
                player.getTPlayer().sendMessage(getFormat("no-interact"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onProjectileLaunch(ProjectileLaunchEvent event) {
        if ((event.getEntity().getType() != EntityType.SNOWBALL)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        PlayerType player = resolvePlayer((Player) event.getEntity().getShooter());
        onSnowballThrow(player);
    }

    @EventHandler
    public final void onEntityInteract(EntityInteractEvent event) {
        onEntityInteract(event.getEntity(), event);
    }

    @EventHandler
    public final void onHangingDestroy(HangingBreakByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        PlayerType player = resolvePlayer((Player) event.getEntity());
        if (!isIngame(player)) {
            return;
        }
        if (canBuild(player) && !isSpectating(player)) {
            return;
        }
        player.getTPlayer().sendMessage(getFormat("not-allowed-spectator"));
        event.setCancelled(true);
    }

    @EventHandler
    public final void onEntityAttack(EntityDamageByEntityEvent event) {
        Entity eventDamager = event.getDamager();
        Entity eventTarget = event.getEntity();
        onDamage(eventDamager, eventTarget, event);
        if (!((eventDamager instanceof Player) || (eventTarget instanceof Player) || (eventDamager instanceof Arrow) || (eventDamager instanceof ThrownPotion))) {
            return;
        }
        if (eventDamager instanceof Player || eventDamager instanceof Arrow || eventDamager instanceof ThrownPotion) {
            if (eventDamager instanceof Arrow) {
                eventDamager = (Entity) ((Arrow) eventDamager).getShooter();
                if (!(eventDamager instanceof Player)) {
                    return;
                }
            }
            if (eventDamager instanceof ThrownPotion) {
                eventDamager = (Entity) ((ThrownPotion) eventDamager).getShooter();
                if (!(eventDamager instanceof Player)) {
                    return;
                }
                if (eventDamager.equals(eventTarget)) {
                    return;
                }
            }
            PlayerType damager = resolvePlayer((Player) eventDamager);
            if (this.gameMeta.pvpMode() == GameMeta.PvPMode.NoPvP) {
                damager.getTPlayer().sendMessage(getFormat("no-pvp-allowed"));
                event.setCancelled(true);
                return;
            }
            if (isSpectating(damager)) {
                damager.getTPlayer().sendMessage(getFormat("not-allowed-spectator"));
                event.setCancelled(true);
                return;
            }
            if (eventTarget instanceof Player) {
                PlayerType target = resolvePlayer((Player) eventTarget);
                double damage = damageForHit(damager, target, event.getDamage());
                if (isSpectating(target)) {
                    event.setCancelled(true);
                    return;
                }
                if (damage != -1) {
                    event.setDamage(damage);
                }
                if (useEnderBar(damager)) {
                    EnderBar.setHealthPercent(damager, ((float) target.getPlayer().getHealth() / (float) target.getPlayer().getMaxHealth()));
                }
                if (!canPvP(damager, target)) {
                    damager.getTPlayer().sendMessage(getFormat("no-pvp", new String[]{"<player>", target.getPlayer().getName()}));
                    event.setCancelled(true);
                    return;
                }
                PlayerGameDamageEvent callingEvent = new PlayerGameDamageEvent(this, target, event.getDamage(), false);
                Bukkit.getPluginManager().callEvent(callingEvent);
                if (callingEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                event.setDamage(callingEvent.getDamage());
                PlayerGameDamageByPlayerEvent callingEvent2 = new PlayerGameDamageByPlayerEvent(callingEvent, damager);
                Bukkit.getPluginManager().callEvent(callingEvent2);
                if (callingEvent2.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                event.setDamage(callingEvent2.getDamage());
            }
        } else if (eventDamager instanceof LivingEntity) {
            PlayerType target = resolvePlayer((Player) eventTarget);
            PlayerGameDamageEvent callingEvent = new PlayerGameDamageEvent(this, target, event.getDamage(), false);
            Bukkit.getPluginManager().callEvent(callingEvent);
            if (callingEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerDied(PlayerDeathEvent event) {
        event.getEntity().setHealth(event.getEntity().getHealthScale());
        Player deadPlayer = event.getEntity();
        final PlayerType dead = resolvePlayer(deadPlayer);
        EntityDamageEvent.DamageCause cause = deadPlayer.getLastDamageCause().getCause();
        List<ItemStack> drops = event.getDrops();
        ItemStack[] itemStacks = drops.toArray(new ItemStack[drops.size()]);
        for (ItemStack stack : itemStacks) {
            if (!canDropItem(dead, stack)) {
                event.getDrops().remove(stack);
            }
        }
        if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            //Process a PvP/PvE encounter
            final EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent)deadPlayer.getLastDamageCause();
            if (deadPlayer.getKiller() != null) {
                final PlayerType player = resolvePlayer(deadPlayer.getKiller());
                Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        playerKilledPlayer(player, dead);
                    }
                }, 2L);
            }
            else {
                fakeDeath(dead);
                Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        playerKilled(dead, (LivingEntity) entityDamageByEntityEvent.getEntity());
                    }
                }, 2L);
            }
            return;
        }
        fakeDeath(dead);
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run() {
                onDeath(dead);
            }
        }, 2L);
        broadcast(getFormat("solo-death", new String[]{"<victim>", dead.getPlayer().getDisplayName()}));
    }

    private void playerKilledPlayer(final PlayerType damager, final PlayerType target) {
        this.tracker.trackKill(damager, target);
        fakeDeath(target);
        this.playerKilled(target, damager);
        PlayerGameKillEvent event = new PlayerGameKillEvent(this, target, damager);
        Bukkit.getPluginManager().callEvent(event);
        broadcast(getFormat("death-message", new String[]{"<killer>", damager.getPlayer().getDisplayName()}, new String[]{"<victim>", target.getPlayer().getDisplayName()}));
    }

    @EventHandler
    public final void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if ((event instanceof EntityDamageByEntityEvent)) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Arrow || damager instanceof Player) {
                return;
            }
            if (damager instanceof ThrownPotion && !event.getEntity().equals(damager)) {
                return;
            }
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        PlayerType player = resolvePlayer((Player) event.getEntity());
        if (!isIngame(player)) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            player.getTPlayer().teleport(player.getPlayer().getLocation().add(0, 1, 0));
        }
        if (isSpectating(player)) {
            event.setCancelled(true);
            return;
        }
        if (onFallDamage(player, event) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        PlayerGameDamageEvent callingEvent = new PlayerGameDamageEvent(this, player, event.getDamage(), false);
        Bukkit.getPluginManager().callEvent(callingEvent);
        if (callingEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    //NOTICE Static Strings!
    @SafeVarargs
    protected final void displayWinners(PlayerType... players) {
        List<String> strings = new ArrayList<>();
        //char[] emptyStrings = new char[64];
        //Arrays.fill(emptyStrings, ' ');
        String line = ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 64);
        strings.add(line);
        for (int x = 0, l = progressiveWinColors.length; x < players.length; x++) {
            int place = x+1;
            float percentage = x == 0 ? 0f : (float)x/players.length;
            int index = Double.valueOf(Math.floor(l * percentage)).intValue();
            ChatColor color = progressiveWinColors[index];
            strings.add("  " + color + players[x].getUsername() + ChatColor.GRAY + " - " + color + String.valueOf(place) + NumberSuffixes.getForString(String.valueOf(place)).getSuffix() + " place.");
        }
        /*while (strings.size() < 9) {
            strings.add(" ");
        }*/
        strings.add(line);
        for (PlayerType player : allPlayers()) {
            TPlayer tPlayer = player.getTPlayer();
            for (String s : strings) {
                tPlayer.sendMessage(s);
            }
        }
    }

    @EventHandler
    public final void onArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        PlayerType shooter = resolvePlayer((Player) event.getEntity());
        if (!isIngame(shooter)) {
            return;
        }
        if (isSpectating(shooter)) {
            shooter.getTPlayer().sendMessage(getFormat("not-allowed-spectator"));
            event.setCancelled(true);
            return;
        }
        if (!canUse(shooter)) {
            shooter.getTPlayer().sendMessage(getFormat("no-shoot"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockBreak(BlockBreakEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (!isIngame(player)) {
            return;
        }
        if (isSpectating(player)) {
            player.getTPlayer().sendMessage(getFormat("not-allowed-spectator"));
            event.setCancelled(true);
            return;
        }
        if (!canBreak(player, event.getBlock())) {
            Material type = event.getBlock().getType();
            if (!(type == Material.LONG_GRASS || type == Material.TNT || type == Material.CROPS)) {
                player.getTPlayer().sendMessage(getFormat("no-break"));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockPlace(BlockPlaceEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (!isIngame(player)) return;
        if (isSpectating(player)) {
            player.getTPlayer().sendMessage(getFormat("not-allowed-spectator"));
            event.setCancelled(true);
            return;
        }
        if (!canPlace(player, event.getBlockPlaced())) {
            player.getTPlayer().sendMessage(getFormat("no-place"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (!isIngame(player)) {
            return;
        }
        /** Comment out not needed because arrows and stuff go through spectators. Add back if wanted.
        if (isSpectating(player)) {
            if (event.getTo().add(0, -8, 0).getBlock().getType() != Material.AIR) {
                event.getPlayer().setVelocity(player.getPlayer().getLocation().getDirection().add(new Vector(0, 8, 0)));
                event.getPlayer().sendMessage(getFormat("spectator-hover"));
                if (!event.getPlayer().getAllowFlight()) event.getPlayer().setAllowFlight(true);
            }
            return;
        }
         */
        if (!canMove(player)) {
            if (event.getTo().getBlock().getX() != event.getFrom().getBlock().getX() || event.getTo().getBlock().getZ() != event.getFrom().getBlock().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (!isIngame(player)) {
            return;
        }
        if (!canPlayerRespawn(player)) {
            makeSpectator(player);
            return;
        }
        event.setRespawnLocation(playerRespawn(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (!isIngame(player)) return;
        if (isSpectating(player) || !canDropItem(player, event.getItemDrop().getItemStack())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        PlayerType player = resolvePlayer((Player) event.getEntity());
        if (!isIngame(player)) return;
        if (isSpectating(player)) event.setCancelled(true);
        if (!allowHunger(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onItemPickup(PlayerPickupItemEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (isSpectating(player)) {
            event.setCancelled(true);
            return;
        }
        if (!this.canPickup(player, event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (this.getArena().getWorld().equals(entity.getLocation().getWorld()) && !allowEntitySpawn(entity)) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public final void onEggThrow(PlayerEggThrowEvent event) {
        onEggThrow(resolvePlayer(event.getPlayer()), event);
    }

    @EventHandler
    public final void onInventoryChange(InventoryClickEvent event) {
        if (!this.allowInventoryChange()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onPlayerChat(AsyncPlayerChatEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (isSpectating(player)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getFormat("spectating-chat"));
        }
    }

    @EventHandler
    public final void onEXPChange(PlayerExpChangeEvent event) {
        PlayerType player = resolvePlayer(event.getPlayer());
        if (!isIngame(player)) {
            return;
        }
        if (isSpectating(player) || !canPickupEXP(player)) {
            event.setAmount(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBoat(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        PlayerType damager = resolvePlayer((Player) event.getDamager());
        if (!isSpectating(damager)) return;
        event.setCancelled(true);
        damager.getTPlayer().sendMessage(getFormat("not-allowed-spectator"));
    }

    protected final void addWin(PlayerType gearzPlayer) {
        TPlayer tPlayer = gearzPlayer.getTPlayer();
        Integer gameWins = (Integer) tPlayer.getStorable(getPlugin(), new GameWins(this));
        if (gameWins == null) {
            gameWins = 0;
        }
        gameWins += 1;
        GameWins newGameWins = new GameWins(this);
        newGameWins.setWins(gameWins);
        tPlayer.store(getPlugin(), newGameWins);
    }

    public final boolean hasEnded(PlayerType player) {
        return this.endedPlayers.contains(player);
    }

    public final void registerExternalListeners(Listener listener) {
        getPlugin().registerEvents(listener);
    }

    protected final PlayerType resolvePlayer(Player player) {
        return this.playerProvider.getPlayerFromPlayer(player);
    }

    /**
     * Get's the games meta
     *
     * @return GameMeta ~ the game's meta
     */
    protected final GameMeta getGameMeta() {
        return this.gameMeta;
    }

    @Data
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private final class GameWins implements TPlayerStorable {
        @NonNull
        private final GearzGame<PlayerType> game;
        private Integer wins;

        @Override
        public String getName() {
            return game.getGameMeta().key() + "_" + "wins";
        }

        @Override
        public Object getValue() {
            return wins == null ? 0 : wins;
        }
    }

    @RequiredArgsConstructor
    @Data
    @EqualsAndHashCode
    private final class SpectatorReminder implements Runnable {

        @NonNull
        private final GearzGame<PlayerType> game;

        @Override
        public void run() {
            for (PlayerType player : game.getSpectators()) {
                if (game.hasEnded(player)) {
                    continue;
                }
                player.getTPlayer().sendMessage(game.getFormat("spectator-ingame"));
            }
        }
    }
}
