package net.tbnr.gearz.game;

import com.mongodb.BasicDBObject;
import lombok.*;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.arena.Arena;
import net.tbnr.gearz.effects.EnderBar;
import net.tbnr.gearz.event.game.GameEndEvent;
import net.tbnr.gearz.event.game.GamePreStartEvent;
import net.tbnr.gearz.event.game.GameStartEvent;
import net.tbnr.gearz.event.player.*;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.BlockRepair;
import net.tbnr.util.InventoryGUI;
import net.tbnr.util.RandomUtils;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerStorable;
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

import java.util.*;

/**
 * GearzGame is a class to represent a game.
 */
@SuppressWarnings({"NullArgumentToVariableArgMethod", "DefaultFileTemplate", "UnusedDeclaration"})
@EqualsAndHashCode(of = {"id", "arena", "players"}, doNotUseGetters = true)
@ToString(of = {"arena", "id", "running", "players", "spectators", "gameMeta"})
public abstract class GearzGame implements Listener {
    private final Arena arena;
    private Set<GearzPlayer> players;
    private Set<GearzPlayer> spectators;
    private Set<GearzPlayer> addedPlayers;
    private Set<GearzPlayer> endedPlayers;
    private InventoryGUI spectatorGui;
    private final GameMeta gameMeta;
    private final GearzPlugin plugin;
    private final Integer id;
    private GearzMetrics metrics;
    @Getter(AccessLevel.PROTECTED) private PvPTracker tracker;
    @Getter private boolean running;
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
        OTHER('*', "rd");

        private String suffix;
        private char numberCharacter;

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
    /**
     * You only get points if you leave on good terms
     */
    private HashMap<GearzPlayer, Integer> pendingPoints;

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
    public GearzGame(List<GearzPlayer> players, Arena arena, GearzPlugin plugin, GameMeta meta, Integer id) {
        this.arena = arena;
        /*this.players = players;
        this.spectators = new ArrayList<>();*/
        this.players = new HashSet<>();
        this.addedPlayers = new HashSet<>();
        for (GearzPlayer player : players) {
            if (player.isValid()) this.players.add(player);
        }

        for (GearzPlayer player : players) {
            if (Gearz.getInstance().showDebug()) {
                Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzGame|73>--------< <init> / player loop has been CAUGHT for: " + player.getUsername());
            }
        }
        this.tracker = new PvPTracker(this);
        this.spectators = new HashSet<>();
        this.pendingPoints = new HashMap<>();
        this.endedPlayers = new HashSet<>();
        this.plugin = plugin;
        this.gameMeta = meta;
        this.id = id;
        this.metrics = GearzMetrics.beginTracking(this);
        this.spectatorGui = new InventoryGUI(getPlayersForMenu(), ChatColor.RED + "Spectator menu.", new InventoryGUI.InventoryGUICallback() {
            @Override
            public void onItemSelect(InventoryGUI gui, InventoryGUI.InventoryGUIItem item, Player player) {
                Player target = Bukkit.getServer().getPlayer(item.getName());
                if (target == null) return;
                player.teleport(target.getLocation());
                player.closeInventory();
                player.sendMessage(getFormat("spectator-tp", new String[]{"<player>", target.getName()}));
                GearzPlayer.playerFromPlayer(player).getTPlayer().playSound(Sound.ENDERMAN_TELEPORT);
                GearzPlayer.playerFromPlayer(player).getTPlayer().playSound(Sound.ARROW_HIT);
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
        for (GearzPlayer player : this.getPlayers()) {
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
                GearzPlayer gearzPlayer = GearzPlayer.playerFromPlayer(p.getPlayer());
                gearzPlayer.sendException(t);
            }
        }
        for (GearzPlayer player2 : this.getPlayers()) {
            try {
                Location location = playerRespawn(player2);
                player2.getTPlayer().teleport(location);
                activatePlayer(player2);
            } catch (Throwable t) {
                //player2.sendException(t);
            }
        }
        for (Entity e : this.arena.getWorld().getEntitiesByClasses(LivingEntity.class)) {
            if (e instanceof Player) {
                continue;
            }
            if (!allowEntitySpawn(e)) {
                e.remove();
            }
        }
        this.tracker.startGame();
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
    }

    public final boolean isIngame(GearzPlayer player) {
        return this.allPlayers().contains(player);
    }

    protected abstract void gameStarting();

    protected final void stopGameForPlayer(GearzPlayer player, GameStopCause cause) {
        if (this.endedPlayers.contains(player)) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new PlayerGameLeaveEvent(player, this));
        player.getTPlayer().resetPlayer();
        if (cause == GameStopCause.GAME_END) {
            if (!this.addedPlayers.contains(player)) {
                int points = 0;
                if (this.pendingPoints.containsKey(player)) {
                    points = this.pendingPoints.get(player);
                }
                player.addPoints(points);
                player.addXp(xpForPlaying());
                player.getTPlayer().sendMessage(getFormat("xp-earned", new String[]{"<xp>", String.valueOf(xpForPlaying())}));
                player.getTPlayer().sendMessage(getFormat("points-earned", new String[]{"<points>", String.valueOf(points)}));
            }
        } else {
            player.getTPlayer().sendMessage(getFormat("game-void"));
        }
        if (player.isValid()) {
            player.setHideStats(false);
        }
        this.endedPlayers.add(player);
    }

    private void stopGame(GameStopCause cause) {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.gameEnding();
        for (GearzPlayer player : allPlayers()) {
            stopGameForPlayer(player, cause);
        }
        broadcast(getFormat("game-ending"));
        this.metrics.finishGame();
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this));
        HandlerList.unregisterAll(this);
        this.plugin.getGameManager().gameEnded(this);
        this.tracker.saveKills();
        if (cause == GameStopCause.GAME_END) {
            BasicDBObject pointsEarned = new BasicDBObject();
            for (Map.Entry<GearzPlayer, Integer> entry : this.pendingPoints.entrySet()) {
                pointsEarned.put(entry.getKey().getPlayer().getName(), entry.getValue());
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("points-earned", pointsEarned);
            this.metrics.done(data);
        }
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
        stopGame(GameStopCause.GAME_END);
    }

    protected abstract void gameEnding();

    /**
     * Whether the player can build
     *
     * @param player ~ the player (in gearzPlayer wrapper)
     * @return boolean ~ true or false whether the player can build
     */
    protected abstract boolean canBuild(GearzPlayer player);

    /**
     * Whether the player can hurt other players (PvP)
     *
     * @param attacker ~ the person that attacked them
     * @param target   ~ the person that is being attacked
     * @return boolean ~ true or false whether the player can pvp
     */
    protected abstract boolean canPvP(GearzPlayer attacker, GearzPlayer target);

    protected abstract boolean canUse(GearzPlayer player);

    protected abstract boolean canBreak(GearzPlayer player, Block block);

    protected abstract boolean canPlace(GearzPlayer player, Block block);

    protected abstract boolean canMove(GearzPlayer player);

    protected abstract boolean canDrawBow(GearzPlayer player);

    protected abstract void playerKilled(GearzPlayer dead, LivingEntity killer);

    protected abstract void playerKilled(GearzPlayer dead, GearzPlayer killer);

    protected abstract void mobKilled(LivingEntity killed, GearzPlayer killer);

    protected abstract boolean canDropItem(GearzPlayer player, Item itemToDrop);

    protected abstract Location playerRespawn(GearzPlayer player);

    protected abstract boolean canPlayerRespawn(GearzPlayer player);

    protected abstract int xpForPlaying();

    protected abstract void activatePlayer(GearzPlayer player);

    protected abstract boolean allowHunger(GearzPlayer player);

    protected void firstActivatePlayer(GearzPlayer player) {
    }

    protected double damageForHit(GearzPlayer attacker, GearzPlayer target, double initialDamage) {
        return -1;
    }

    protected boolean canPickup(GearzPlayer pickupee, Item item) {
        return true;
    }

    protected boolean allowEntitySpawn(Entity entity) {
        return false;
    }

    protected void removePlayerFromGame(GearzPlayer player) {
    }

    protected void onEggThrow(GearzPlayer player, PlayerEggThrowEvent event) {
    }

    protected void onSnowballThrow(GearzPlayer player) {
    }

    protected void onDamage(Entity damager, Entity target, EntityDamageByEntityEvent event) {
    }

    protected void onEntityInteract(Entity entity, EntityInteractEvent event) {
    }

    protected boolean useEnderBar(GearzPlayer player) {
        return true;
    }

    protected boolean allowInventoryChange() {
        return false;
    }

    protected void gamePreStart() {
    }

    protected void onDeath(GearzPlayer player) {
    }

    protected boolean canPickupEXP(GearzPlayer player) {
        return false;
    }

    protected boolean onFallDamage(GearzPlayer player, EntityDamageEvent event) {
        return false;
    }

    protected boolean canLeafsDecay() {
        return false;
    }

    protected Explosion getExplosionType() {
        return Explosion.REPAIR_BLOCK_DAMAGE_AND_NO_DROP;
    }

    protected boolean canUsePotion(GearzPlayer player) {
        return true;
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
     * Add Game Points
     *
     * @param player ~ (in GearzPlayer wrapper) the player to add points to
     * @param points ~ the amount of points to add
     */
    protected final void addGPoints(final GearzPlayer player, Integer points) {
        Integer cPend = this.pendingPoints.containsKey(player) ? this.pendingPoints.get(player) : 0;
        this.pendingPoints.put(player, cPend + points);
        player.getTPlayer().sendMessage(getFormat("points-added", new String[]{"<points>", String.valueOf(points)}));
    }

    /**
     * Get's the players which are playing the game
     *
     * @return List<GearzPlayer> ~ List of players
     */
    public final HashSet<GearzPlayer> getPlayers() {
        HashSet<GearzPlayer> players = new HashSet<>();
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
     * @return List<GearzPlayer> ~ List of Players
     */
    public final HashSet<GearzPlayer> getSpectators() {
        HashSet<GearzPlayer> spectators = new HashSet<>();
        for (GearzPlayer player : this.spectators) {
            if (player.getPlayer() != null) {
                spectators.add(player);
            }
        }
        return spectators;
    }

    public final void addPlayer(GearzPlayer player) {
        if (Gearz.getInstance().showDebug()) {
            Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzGame|334>--------< addPlayer has been CAUGHT for: " + player.getUsername());
        }
        this.addedPlayers.add(player);
        this.endedPlayers.add(player);
        makeSpectator(player);
    }

    /**
     * Check's whether a player is playing
     *
     * @param player ~ (in GearzPlayer wrapper) to be checked
     * @return boolean ~ true or false whether the player is playing
     */
    public final boolean isPlaying(GearzPlayer player) {
        return this.players.contains(player);
    }

    public final boolean isSpectating(GearzPlayer player) {
        return this.spectators.contains(player);
    }

    /**
     * Turn's a player into a spectator
     *
     * @param player ~ (in GearzPlayer wrapper) to become a spectator
     */
    protected final void makeSpectator(GearzPlayer player) {
        player.getTPlayer().resetPlayer();
        this.spectators.add(player);
        Bukkit.getPluginManager().callEvent(new PlayerBeginSpectateEvent(player, this));
        player.getTPlayer().sendMessage(getFormat("begin-spectating"));
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
        player.getPlayer().setAllowFlight(true);
        player.getPlayer().setFlying(true);
        //player.getTPlayer().addPotionEffect(PotionEffectType.INVISIBILITY);
        hideFromAll(player);
        player.getTPlayer().playSound(Sound.FIZZ);
        if (isPlaying(player)) {
            this.players.remove(player);
        } else {
            try {
                //playerRespawn(player); // WUT, that just gets a location...
                player.getTPlayer().teleport(playerRespawn(player));
            } catch (Throwable t) {
                t.printStackTrace();
                player.sendException(t);
            }
        }
        for (GearzPlayer player1 : spectators) {
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
            player.getPlayer().hidePlayer(player1.getPlayer());
        }
        player.setHideStats(false);
        player.getTPlayer().giveItem(Material.BOOK, 1, (short) 0, getFormat("spectator-chooser"));
        spectatorGui.updateContents(getPlayersForMenu());
    }

    protected final ArrayList<InventoryGUI.InventoryGUIItem> getPlayersForMenu() {
        ArrayList<InventoryGUI.InventoryGUIItem> items = new ArrayList<>();
        try {
            for (GearzPlayer player : getPlayers()) {
                if (Gearz.getInstance().showDebug()) {
                    Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzGame|399>--------< getPlayersForMenu / player loop has been CAUGHT for: " + player.toString());
                }
                if (!player.isValid()) {
                    continue;
                }

                ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                ItemMeta itemMeta = stack.getItemMeta();
                itemMeta.setDisplayName(getGameMeta().mainColor() + player.getPlayer().getName());
                stack.setItemMeta(itemMeta);
                //stack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
                items.add(new InventoryGUI.InventoryGUIItem(stack, player.getUsername()));
            }
        } catch (NullPointerException npe) {
            if (Gearz.getInstance().showDebug()) {
                Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzGame|416>--------< getPlayersForMenu / player loop has thrown a npe: " + npe.toString());
            }
            if (Gearz.getInstance().showDebug()) {
                Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzGame|417>--------< getPlayersForMenu / player loop has thrown a npe: " + npe.getCause());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return items;
    }

    /**
     * Hide's a player from all other players in the game (inc spectators)
     *
     * @param player ~ (in GearzPlayer wrapper) to be hidden
     */
    private void hideFromAll(GearzPlayer player) {
        for (GearzPlayer player1 : allPlayers()) {
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

    private void showForAll(GearzPlayer player) {
        for (GearzPlayer player1 : allPlayers()) {
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

    protected final void dropItemsFormPlayer(GearzPlayer player) {
        World world = this.arena.getWorld();
        ItemStack[] armorContents = player.getPlayer().getInventory().getArmorContents();
        ItemStack[] inventoryContents = player.getPlayer().getInventory().getContents();
        ItemStack[] all = RandomUtils.concatenate(armorContents, inventoryContents);
        for (ItemStack stack : all) {
            if (stack == null) {
                continue;
            }
            if (stack.getType() == Material.AIR) {
                continue;
            }
            Item item = world.dropItemNaturally(player.getPlayer().getLocation(), stack);
            if (!canDropItem(player, item)) {
                item.remove();
            } else {
                Gearz.getInstance().getLogger().info(player.getUsername() + " dropped " + item.getItemStack().getType() + ":" + item.getItemStack().getAmount());
            }
        }
        player.getTPlayer().clearInventory();
    }

    protected final void fakeDeath(GearzPlayer player) {
        dropItemsFormPlayer(player);
        player.getTPlayer().resetPlayer();
        PlayerGameDeathEvent event = new PlayerGameDeathEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (!canPlayerRespawn(player)) {
            makeSpectator(player);
            return;
        }
        player.getTPlayer().teleport(playerRespawn(player));
        player.getPlayer().playNote(player.getPlayer().getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
        activatePlayer(player);
        PlayerGameRespawnEvent respawnEvent = new PlayerGameRespawnEvent(player, this);
        Bukkit.getPluginManager().callEvent(respawnEvent);
    }

    protected final void makePlayer(GearzPlayer player) {
        if (this.endedPlayers.contains(player)) {
            throw new IllegalStateException("You cannot restart a game for a player whom is ended");
        }
        if (!this.players.contains(player)) {
            this.players.add(player);
        }
        if (this.spectators.contains(player)) {
            this.spectators.remove(player);
        }
        this.pendingPoints.put(player, 0);
        player.setHideStats(true);
        player.getTPlayer().resetPlayer();
        showForAll(player);
        player.setGame(this);
    }

    /**
     * Removes a player from the game, depending on if there spectating or not it will give different GameStopCause
     *
     * @param player ~ (in GearzPlayer wrapper) to be removed
     */
    public final void removePlayer(GearzPlayer player) {
        if (!this.isRunning()) {
            return;
        }
        if (Gearz.getInstance().showDebug()) {
            Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzGAme|483>--------< removePlayer has been CAUGHT for: " + player.getUsername());
        }
        GameStopCause cause = isSpectating(player) ? GameStopCause.GAME_END : GameStopCause.FORCED;
        playerLeft(player);
        stopGameForPlayer(player, cause);
        plugin.getGameManager().spawn(player);
    }

    public final void playerLeft(GearzPlayer player) {
        if (Gearz.getInstance().showDebug()) {
            Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<GearzGAme|490>--------< playerLeft has been CAUGHT for: " + player.getUsername());
        }
        this.players.remove(player);
        this.spectators.remove(player);
        this.pendingPoints.remove(player);
        player.setGame(null);
        if (this.players.size() < 2) {
            stopGame(GameStopCause.FORCED);
        }
        removePlayerFromGame(player);
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
        for (GearzPlayer player : allPlayers()) {
            try {
                player.getTPlayer().sendMessage(message);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Get's all the players including spectators
     *
     * @return List<GearzPlayer> List of players (in GearzPlayer wrapper) inc. Spectators
     */
    public final HashSet<GearzPlayer> allPlayers() {
        HashSet<GearzPlayer> allPlayers = new HashSet<>();
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
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());
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
        GearzPlayer player = GearzPlayer.playerFromPlayer((Player) event.getEntity().getShooter());
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
        GearzPlayer player = GearzPlayer.playerFromPlayer((Player) event.getEntity());
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
                eventDamager = ((Arrow) eventDamager).getShooter();
                if (!(eventDamager instanceof Player)) {
                    return;
                }
            }
            if (eventDamager instanceof ThrownPotion) {
                eventDamager = ((ThrownPotion) eventDamager).getShooter();
                if (!(eventDamager instanceof Player)) {
                    return;
                }
                if (eventDamager.equals(eventTarget)) {
                    return;
                }
            }
            GearzPlayer damager = GearzPlayer.playerFromPlayer((Player) eventDamager);
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
                GearzPlayer target = GearzPlayer.playerFromPlayer((Player) eventTarget);
                double damage = damageForHit(damager, target, event.getDamage());
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
                if (target.getPlayer().getHealth() - event.getDamage() <= 0) {
                    playerKilledPlayer(damager, target);
                    event.setCancelled(true);
                }
            }
        } else if (eventDamager instanceof LivingEntity) {
            GearzPlayer target = GearzPlayer.playerFromPlayer((Player) eventTarget);
            PlayerGameDamageEvent callingEvent = new PlayerGameDamageEvent(this, target, event.getDamage(), false);
            Bukkit.getPluginManager().callEvent(callingEvent);
            if (callingEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            if (target.getPlayer().getHealth() - event.getDamage() <= 0) {
                this.playerKilled(target, (LivingEntity) eventDamager);
                fakeDeath(target);
                event.setCancelled(true);
            }
        }
    }

    private void playerKilledPlayer(final GearzPlayer damager, final GearzPlayer target) {
        this.playerKilled(target, damager);
        this.tracker.trackKill(damager, target);
        fakeDeath(target);
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
        GearzPlayer player = GearzPlayer.playerFromPlayer((Player) event.getEntity());
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
            return;
        }
        if (player.getPlayer().getHealth() - event.getDamage() <= 0) {
            EntityDamageEvent lastDamageCause = player.getPlayer().getLastDamageCause();
            if (lastDamageCause instanceof EntityDamageByEntityEvent && !lastDamageCause.equals(event) && !lastDamageCause.isCancelled()) {
                onEntityAttack((EntityDamageByEntityEvent) lastDamageCause);
                if (lastDamageCause.isCancelled()) {
                    return;
                }
            }
            onDeath(player);
            fakeDeath(player);
            event.setCancelled(true);
            broadcast(getFormat("solo-death", new String[]{"<victim>", player.getPlayer().getDisplayName()}));
        }
    }

        //NOTICE Static Strings!
    protected final void displayWinners(GearzPlayer... players) {
        List<String> strings = new ArrayList<>();
        char[] emptyStrings = new char[64];
        Arrays.fill(emptyStrings, ' ');
        String line = String.valueOf(ChatColor.STRIKETHROUGH) + ChatColor.BLACK + new String(emptyStrings);
        strings.add(line);
        for (int x = 0, l = progressiveWinColors.length; x < players.length; x++) {
            float percentage = x == 0 ? 0f : (float)x/players.length;
            int index = Double.valueOf(Math.floor(l * percentage)).intValue();
            ChatColor color = progressiveWinColors[index];
            strings.add("  " + color + players[x].getUsername() + ChatColor.GRAY + " - " + color + String.valueOf(x) + NumberSuffixes.getForString(String.valueOf(x)).getSuffix() + " place.");
        }
        while (strings.size() < 9) {
            strings.add(" ");
        }
        strings.add(line);
        for (GearzPlayer player : allPlayers()) {
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
        GearzPlayer shooter = GearzPlayer.playerFromPlayer((Player) event.getEntity());
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
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());
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
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());
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
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());

        if (!isIngame(player)) {
            return;
        }
        if (isSpectating(player)) {
            return;
        }
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
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());
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
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());
        if (!isIngame(player)) {
            return;
        }
        if (isSpectating(player) || !canDropItem(player, event.getItemDrop())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        GearzPlayer player = GearzPlayer.playerFromPlayer((Player) event.getEntity());
        if (!isIngame(player)) {
            return;
        }
        if (isSpectating(player)) {
            event.setCancelled(true);
        }
        if (!allowHunger(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onItemPickup(PlayerPickupItemEvent event) {
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());
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
        onEggThrow(GearzPlayer.playerFromPlayer(event.getPlayer()), event);
    }

    @EventHandler
    public final void onInventoryChange(InventoryClickEvent event) {
        if (!this.allowInventoryChange()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onPlayerChat(AsyncPlayerChatEvent event) {
        GearzPlayer gearzPlayer = GearzPlayer.playerFromPlayer(event.getPlayer());
        if (isSpectating(gearzPlayer)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getFormat("spectating-chat"));
        }
    }

    @EventHandler
    public final void onEXPChange(PlayerExpChangeEvent event) {
        GearzPlayer player = GearzPlayer.playerFromPlayer(event.getPlayer());
        if (!isIngame(player)) {
            return;
        }
        if (isSpectating(player) || !canPickupEXP(player)) {
            event.setAmount(0);
        }
    }

    protected final void addWin(GearzPlayer gearzPlayer) {
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

    public final boolean hasEnded(GearzPlayer player) {
        return this.endedPlayers.contains(player);
    }

    public final void registerExternalListeners() {
        getPlugin().registerEvents(this);
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
    private final static class GameWins implements TPlayerStorable {
        @NonNull
        private GearzGame game;
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
    private final static class SpectatorReminder implements Runnable {

        @NonNull
        private GearzGame game;

        @Override
        public void run() {
            for (GearzPlayer player : game.getSpectators()) {
                if (game.hasEnded(player)) {
                    continue;
                }
                player.getTPlayer().sendMessage(game.getFormat("spectator-ingame"));
            }
        }
    }
}
