package net.tbnr.gearz.game.voting;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.game.*;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.player.TPlayerDisconnectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Voting Session is used to create a timed vote on many objects from many players
 */
public class ChestVotingSession extends VotingSession implements Listener, GameCountdownHandler {
    /**
     * Holds all the Inventory objects being displayed to the players.
     */
    private Map<GearzPlayer, Inventory> votingViews;
    /**
     * Holds a list of all the times you can vote fof
     */
    private List<Votable> votables;
    /**
     * Holds who voted for what so that it can be changed later (keeping track of stuff is important :P)
     */
    private Map<GearzPlayer, Votable> votesCast;
    /**
     * This saves CPU cycles by keeping a tally of the number of votes each votable has, rather than for looping it every time.
     */
    private Map<Votable, Integer> countedVotes; //Saves CPU cycles to calculate this in real time.
    /**
     * Stores which items go with which votable item for which players. This is used to update quantities.
     */
    private Map<GearzPlayer, Map<Votable, ItemStack>> repItems;

    private Map<Votable, Integer> slotNumbers;
    /**
     * The handler that this object should delegate to.
     */
    private VotingHandler handler;
    /**
     * The GameManagerMultiGame that is responsible for creating this VotingSession. Used for scheduler stuff.
     */
    private GameManager gameManager;
    /**
     * The amount of time given for this voting session.
     */
    private Integer timeCountodwn;
    /**
     * Is our voting complete?
     */
    private boolean votingDone;
    /**
     * Allows the player to close the window when they've voted.
     */
    @Getter @Setter
    private boolean closingWhenVoted = false;

    /**
     * This creates, but does not start (stages) a voting session with a bunch of params.
     *
     * @param playerList    The players whom are voting on this.
     * @param votables      The items being voted upon.
     * @param manager       The manager that created this. Used for color codes, scheduler, and some other stuff.
     * @param timeCountodwn The amount of time given to vote on these items
     * @param handler       The handler that will be given calls back.
     */
    public ChestVotingSession(List<GearzPlayer> playerList, List votables, GameManager manager, Integer timeCountodwn, VotingHandler handler) {
        //Set our variables
        this.votables = new ArrayList<>(); //Weed out the non-votables from the passed list in the for loop below.
        this.handler = handler;
        this.votingViews = new HashMap<>();
        this.votesCast = new HashMap<>();
        this.countedVotes = new HashMap<>();
        this.repItems = new HashMap<>();
        this.gameManager = manager;
        this.timeCountodwn = timeCountodwn;
        this.slotNumbers = new HashMap<>();
        for (Object o : votables) {
            if (o instanceof Votable) {
                this.votables.add((Votable) o);
            }
        }
        //Calculate the size of the voting interface in real time.
        //Create an inventory view for every player, and store it.
        for (GearzPlayer player : playerList) {
            setupPlayer(player);
        }
        this.votingDone = false; //And, we're voting!
    }

    private void setupPlayer(GearzPlayer player) {
        int size = Math.max(Double.valueOf(9 * (Math.ceil((this.votables.size() + 1) / 9))).intValue(), 9);
        assert size % 9 == 0; //Lets hope I got this right :c
        Inventory inventory = Bukkit.createInventory(player.getPlayer(), size, format("game-strings.voting-chest-title", this.gameManager.getGameMeta()));
        Map<Votable, ItemStack> itemStackMap = new HashMap<>();
        Integer index = 0;
        for (Object arena1 : votables) {
            if (!(arena1 instanceof Votable)) {
                continue; //Uh oh.
            }
            Votable arena = (Votable) arena1; //Cast it.
            if (!this.votables.contains(arena)) {
                this.votables.add(arena);
            }
            ItemStack repItem = new ItemStack(Material.MAP);
            repItem.setAmount(1);
            ItemMeta meta = repItem.getItemMeta();
            meta.setDisplayName(format("game-strings.map-title", gameManager.getGameMeta(), new String[]{"<name>", arena.getName()}));
            List<String> lore = new ArrayList<>();
            lore.add(gameManager.getGameMeta().secondaryColor() + "————————————————————————————————");
            lore.add(format("game-strings.map-lore-author", gameManager.getGameMeta(), new String[]{"<author>", arena.getAuthors()}));
            lore.add(format("game-strings.map-lore-description", gameManager.getGameMeta(), new String[]{"<description>", arena.getDescription()}));
            lore.add(gameManager.getGameMeta().secondaryColor() + "————————————————————————————————");
            meta.setLore(lore);
            repItem.setItemMeta(meta);
            inventory.addItem(repItem);
            itemStackMap.put(arena, repItem);
            if (!this.slotNumbers.containsKey(arena1)) {
                this.slotNumbers.put(arena, index);
            }
            index++;
        }
        //Create the clock that tells you how much time you have.
        this.repItems.put(player, itemStackMap);
        ItemStack clock = new ItemStack(Material.WATCH);
        clock.setAmount(timeCountodwn); //Countdown!
        clock.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 1);
        ItemMeta itemMeta = clock.getItemMeta();
        itemMeta.setDisplayName(format("game-strings.voting-clock-title", gameManager.getGameMeta()));
        clock.setItemMeta(itemMeta);
        inventory.setItem(size - 1, clock);
        this.votingViews.put(player, inventory);
    }

    /**
     * Starts the voting procedure, registers for events, and opens the interfaces.
     */
    public void start() {
        this.gameManager.getPlugin().registerEvents(this);
        GameCountdown countdown = new GameCountdown(timeCountodwn, this, this.gameManager.getPlugin());
        countdown.start();
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

    /**
     * Formats a simple string
     *
     * @param s    The string to format
     * @param meta The meta of the game
     * @return The formatted text.
     */
    private String format(String s, GameMeta meta) {
        return this.format(s, meta, null);
    }

    @Override
    public void onCountdownStart(Integer max, GameCountdown countdown) {
        for (Map.Entry<GearzPlayer, Inventory> gearzPlayerInventoryEntry : votingViews.entrySet()) {
            gearzPlayerInventoryEntry.getKey().getPlayer().openInventory(gearzPlayerInventoryEntry.getValue());
        }
    }

    @Override
    public void onCountdownChange(Integer seconds, Integer max, GameCountdown countdown) {
        for (Map.Entry<GearzPlayer, Inventory> gearzPlayerInventoryEntry : this.votingViews.entrySet()) {
            ItemStack item = gearzPlayerInventoryEntry.getValue().getItem(8);
            item.setAmount(seconds);
        }
    }

    @Override
    public void onCountdownComplete(GameCountdown countdown) {
        endVoting();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryCloseEvent(final InventoryCloseEvent event) {
        if (votingDone) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        GearzPlayer player = GearzPlayer.playerFromPlayer((Player) event.getPlayer());
        if (!this.votingViews.containsKey(player)) {
            return;
        }
        if (this.votesCast.containsKey(player) && this.closingWhenVoted) {
            return;
        }
        final Inventory i = votingViews.get(player);
        Bukkit.getScheduler().runTaskLater(this.gameManager.getPlugin(), new Runnable() {
            @Override
            public void run() {
                event.getPlayer().openInventory(i);
            }
        }, 5);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (this.votingDone) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        GearzPlayer player = GearzPlayer.playerFromPlayer((Player) event.getWhoClicked());
        if (!this.votingViews.containsKey(player)) {
            return;
        }
        event.setCancelled(true);
        boolean cont = false;
        switch (event.getClick()) {
            case RIGHT:
            case LEFT:
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
            case MIDDLE:
            case NUMBER_KEY:
            case DROP:
                cont = true;
        }
        if (!cont) {
            return;
        }
        Votable votable = null;
        Map<Votable, ItemStack> votableItemStackMap = repItems.get(player);
        if (votableItemStackMap == null) {
            return;
        }
        for (Votable v : this.votables) {
            if (event.getCurrentItem().equals(votableItemStackMap.get(v))) {
                votable = v;
                break;
            }
        }
        if (votable == null) {
            return;
        }
        Votable votable1 = this.votesCast.get(player);
        if (votable.equals(votable1)) {
            return;
        }
        if (votable1 != null) {
            ItemStack itemStack = votableItemStackMap.get(votable1);
            itemStack.removeEnchantment(Enchantment.FIRE_ASPECT);
            itemStack.setAmount(Math.max(itemStack.getAmount() - 1, 1));
            Integer integer1 = this.slotNumbers.get(votable1);
            Integer integer = this.countedVotes.get(votable1);
            this.countedVotes.put(votable1, integer - 1);
            for (Inventory view : this.votingViews.values()) {
                view.setItem(integer1, itemStack);
            }
        }
        this.votesCast.put(player, votable);
        Integer integer = this.countedVotes.get(votable);
        if (integer == null) {
            integer = 0;
        }
        this.countedVotes.put(votable, integer + 1);
        for (Map.Entry<GearzPlayer, Map<Votable, ItemStack>> gearzPlayerMapEntry : this.repItems.entrySet()) {
            ItemStack itemStack = gearzPlayerMapEntry.getValue().get(votable);
            itemStack.setAmount(integer + 1);
            if (this.votesCast.get(gearzPlayerMapEntry.getKey()).equals(votable)) {
                itemStack.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
            } else {
                itemStack.removeEnchantment(Enchantment.FIRE_ASPECT);
            }
            this.votingViews.get(gearzPlayerMapEntry.getKey()).setItem(event.getSlot(), itemStack);
        }
        boolean allVoted = true;
        for (GearzPlayer gearzPlayer : this.votingViews.keySet()) {
            if (this.votesCast.get(gearzPlayer) == null) {
                allVoted = false;
                break;
            }
        }
        if (allVoted) {
            endVoting();
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerLeaveEvent(TPlayerDisconnectEvent event) {
        GearzPlayer gearzPlayer = GearzPlayer.playerFromTPlayer(event.getPlayer());
        Votable remove = this.votesCast.remove(gearzPlayer);
        this.repItems.remove(gearzPlayer);
        this.votingViews.remove(gearzPlayer);
        for (Map.Entry<GearzPlayer, Map<Votable, ItemStack>> gearzPlayerMapEntry : this.repItems.entrySet()) {
            Inventory itemStacks = this.votingViews.get(gearzPlayerMapEntry.getKey());
            int slot = 0;
            while (!itemStacks.getItem(slot).equals(gearzPlayerMapEntry.getValue().get(remove))) {
                slot++;
                if (slot > this.votables.size() + 2) {
                    break;
                }
            }
            ItemStack i = itemStacks.getItem(slot);
            i.setAmount(Math.max(i.getAmount() - 1, 1));
            itemStacks.setItem(slot, i);
            gearzPlayerMapEntry.getValue().put(remove, i);
            this.repItems.put(gearzPlayerMapEntry.getKey(), gearzPlayerMapEntry.getValue());
        }

    }

    public void addPlayer(GearzPlayer player) {
        this.setupPlayer(player);
        player.getPlayer().openInventory(votingViews.get(player));
    }

    private void endVoting() {
        this.votingDone = true;
        for (GearzPlayer player : this.votingViews.keySet()) {
            player.getPlayer().closeInventory();
        }
        this.handler.onVotingDone(this.countedVotes, this);
        HandlerList.unregisterAll(this);
    }

    @SuppressWarnings("unused")
    public void reopenPlayer(GearzPlayer player) {
        if (!this.closingWhenVoted) {
            return;
        }
        player.getPlayer().openInventory(this.votingViews.get(player));
    }

    public void extendSession(Integer time) {
        this.votingDone = false;
        this.timeCountodwn = time;
        this.start();
    }
}
