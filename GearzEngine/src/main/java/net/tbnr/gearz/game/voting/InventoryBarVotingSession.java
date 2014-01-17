package net.tbnr.gearz.game.voting;

import com.comphenix.protocol.utility.MinecraftReflection;
import lombok.Getter;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.game.*;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.MapImageRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joey on 12/11/13.
 */
public final class InventoryBarVotingSession extends VotingSession implements Listener, GameCountdownHandler {
    @Getter
    private List<Votable> votables;
    @Getter
    private List<GearzPlayer> players;
    @Getter
    private HashMap<Integer, Votable> slots;
    @Getter
    private HashMap<GearzPlayer, Votable> votes;
    @Getter
    private boolean voting;
    private VotingHandler handler;
    private GameManager gameManager;
    private GameCountdown countdown;

    public InventoryBarVotingSession(List<GearzPlayer> players, List votables, VotingHandler handler, GameManager manager) {
        this.players = players;
        this.votables = new ArrayList<>();
        for (Object o : votables) {
            if (o instanceof Votable) {
                this.votables.add((Votable) o);
            }
        }
        this.slots = new HashMap<>();
        this.votes = new HashMap<>();
        this.handler = handler;
        this.voting = false;
        this.gameManager = manager;
    }

    @Override
    public void onCountdownStart(Integer max, GameCountdown countdown) {
        updateWatch(max);
    }

    @Override
    public void onCountdownChange(Integer seconds, Integer max, GameCountdown countdown) {
        updateWatch(seconds);
    }

    @Override
    public void onCountdownComplete(GameCountdown countdown) {
        this.handler.onVotingDone(getVoteCounts(), this);
    }

    public void extendSession(Integer seconds) {
        countdown = new GameCountdown(seconds, this, this.gameManager.getPlugin());
        countdown.start();
    }

    public void endSession() {
        if (!this.countdown.isDone()) {
            this.countdown.stop();
        }
        this.voting = false;
    }

    public void startSession(Integer seconds) {
        this.voting = true;
        for (int x = 0; x < this.votables.size(); x++) {
            this.slots.put(x, this.votables.get(x));
        }
        for (GearzPlayer p : this.players) {
            setupPlayer(p);
        }
        this.gameManager.getPlugin().registerEvents(this);
        extendSession(seconds);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!voting) {
            return;
        }
        int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
        Votable votable = this.slots.get(heldItemSlot);
        if (votable == null) {
            return;
        }
        voteFor(votable, GearzPlayer.playerFromPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!voting) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getWhoClicked();
        if (!(this.players.contains(GearzPlayer.playerFromPlayer(p)))) {
            return;
        }
        event.setCancelled(true);
    }

    public void renderMap(MapView mapView, final Votable v) {
        for (MapRenderer renderer : mapView.getRenderers()) {
            mapView.removeRenderer(renderer);
        }
        mapView.setScale(MapView.Scale.FARTHEST);
        try {
            mapView.addRenderer(new MapImageRenderer(ImageIO.read(Gearz.getInstance().getResource("T.png")), v.getName()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setupPlayer(GearzPlayer player) {
        player.getPlayer().getInventory().clear();
        for (Map.Entry<Integer, Votable> votableIntegerEntry : this.slots.entrySet()) {
            player.getPlayer().getInventory().setItem(votableIntegerEntry.getKey(), getItemStackFor(votableIntegerEntry.getValue()));
        }
        updateWatch(1);
    }

    private ItemStack getItemStackFor(Votable v) {

        MapView mapView = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));
        renderMap(mapView, v);
        ItemStack s = new ItemStack(Material.MAP, 1, mapView.getId());

        //ItemStack s = new ItemStack(Material.MAP, 1);
        ItemMeta meta = s.getItemMeta();
        meta.setDisplayName(format("game-strings.map-title", gameManager.getGameMeta(), new String[]{"<name>", v.getName()}));
        List<String> lore = new ArrayList<>();
        lore.add(gameManager.getGameMeta().secondaryColor() + "————————————————————————————————");
        lore.add(format("game-strings.map-lore-author", gameManager.getGameMeta(), new String[]{"<author>", v.getAuthors()}));
        lore.add(format("game-strings.map-lore-description", gameManager.getGameMeta(), new String[]{"<description>", v.getDescription()}));
        lore.add(gameManager.getGameMeta().secondaryColor() + "————————————————————————————————");
        meta.setLore(lore);
        s.setItemMeta(meta);
        return s;
    }

    private ItemStack getWatch() {
        return new ItemStack(Material.WATCH, 1);
    }

    private void updateWatch(Integer seconds) {
        ItemStack watch = getWatch();
        ItemStack bukkitItemStack = MinecraftReflection.getBukkitItemStack(watch);
        bukkitItemStack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
        bukkitItemStack.setAmount(Math.max(1, seconds));
        for (GearzPlayer p : this.players) {
            p.getPlayer().getInventory().setItem(8, bukkitItemStack);
        }
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

    private void updateVotable(Integer slot) {
        if (slot < 0 || slot > 7) {
            return;
        }
        Votable votable = this.slots.get(slot);
        Map<Votable, Integer> voteCounts = getVoteCounts();
        for (GearzPlayer p : this.players) {
            ItemStack item = p.getPlayer().getInventory().getItem(slot);
            if (item == null) {
                item = getItemStackFor(votable);
            }
            Votable votable1 = this.votes.get(p);
            if (votable1 != null && votable1.equals(votable)) {
                item.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
            }
            if (item == null) {
                continue;
            }
            if (voteCounts == null) {
                continue;
            }
            if (votable == null) {
                continue;
            }
            //item.setAmount(Math.max(1,voteCounts.get(votable)));
            if (voteCounts.get(votable) == null) {
                return;
            }
            item.setAmount(voteCounts.get(votable));
            p.getPlayer().getInventory().setItem(slot, item);
        }
    }

    private void voteFor(Votable v, GearzPlayer player) {
        Votable votable = this.votes.get(player);
        int updateSlot = -1;
        if (votable != null) {
            updateSlot = getSlotFor(votable);
        }
        this.votes.put(player, v);
        Integer slotFor = this.getSlotFor(v);
        if (updateSlot != -1 && updateSlot != slotFor) {
            updateVotable(updateSlot);
        }
        player.getPlayer().sendMessage(Gearz.getInstance().getFormat("formats.voted-for", false, new String[]{"<map>", v.getName()}));
        player.getTPlayer().playSound(Sound.ORB_PICKUP);
        updateVotable(slotFor);
    }

    public Map<Votable, Integer> getVoteCounts() {
        Map<Votable, Integer> voteCounts = new HashMap<>();
        for (Map.Entry<GearzPlayer, Votable> entry : this.votes.entrySet()) {
            PlayerMapVoteEvent event = new PlayerMapVoteEvent(1, entry.getKey(), entry.getValue());
            Bukkit.getPluginManager().callEvent(event);
            voteCounts.put(entry.getValue(), (voteCounts.containsKey(entry.getValue())) ? voteCounts.get(entry.getValue()) + event.getNumberOfVotes() : event.getNumberOfVotes());
        }
        return voteCounts;
    }

    private Integer getSlotFor(Votable v) {
        for (Map.Entry<Integer, Votable> integerVotableEntry : this.slots.entrySet()) {
            if (integerVotableEntry.getValue().equals(v)) {
                return integerVotableEntry.getKey();
            }
        }
        return -1;
    }

    public void addPlayer(GearzPlayer gearzPlayer) {
        this.players.add(gearzPlayer);
        setupPlayer(gearzPlayer);
    }

    public void removePlayer(GearzPlayer gearzPlayer) {
        if (Gearz.getInstance().showDebug()) {
            Gearz.getInstance().getLogger().info("GEARZ DEBUG ---<InventoryBarVotingSession|213>--------< removePlayer has been CAUGHT for: " + gearzPlayer.getUsername());
        }
        this.players.remove(gearzPlayer);
        Votable remove = this.votes.remove(gearzPlayer);
        updateVotable(getSlotFor(remove));
    }
}
