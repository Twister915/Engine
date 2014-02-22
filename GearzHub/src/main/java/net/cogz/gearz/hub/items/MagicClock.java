package net.cogz.gearz.hub.items;

import net.cogz.gearz.hub.GearzHub;
import net.cogz.gearz.hub.annotations.HubItem;
import net.cogz.gearz.hub.annotations.HubItemMeta;
import net.tbnr.util.player.cooldowns.TCooldown;
import net.tbnr.util.player.cooldowns.TCooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jake on 1/16/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@HubItemMeta(
        key = "magicclock"
)
public class MagicClock extends HubItem {
    final List<String> enabledFor = new ArrayList<>();

    public MagicClock() {
        super(true);
    }

    @Override
    public List<ItemStack> getItems() {
	    List<ItemStack> items = new ArrayList<>();
        ItemStack itemStack = new ItemStack(Material.STICK, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(getProperty("name", true));
        itemStack.setItemMeta(meta);
	    items.add(itemStack);

	    ItemStack blazeRod = itemStack.clone();
	    blazeRod.setType(Material.BLAZE_ROD);
	    items.add(blazeRod);
	    return items;
    }

    @Override
    public void rightClicked(Player player) {
        toggle(player);
        handleToggle(player);
    }

    @Override
    public void leftClicked(Player player) {
        rightClicked(player);
    }

    public void toggle(Player player) {
	    if(TCooldownManager.canContinueLocal(player.getName() + "_clock", new TCooldown(TimeUnit.SECONDS.toMillis(3)))) {
	        if (enabledFor.contains(player.getName())) {
		        player.sendMessage(getProperty("toggleOff", true, new String[]{"<prefix>", GearzHub.getInstance().getChatPrefix()}));
		        player.getItemInHand().setType(Material.STICK);
	            enabledFor.remove(player.getName());
	        } else {
		        player.sendMessage(getProperty("toggleOn", true, new String[]{"<prefix>", GearzHub.getInstance().getChatPrefix()}));
		        player.getItemInHand().setType(Material.BLAZE_ROD);
	            enabledFor.add(player.getName());
	        }
		    player.playSound(player.getLocation(), Sound.ARROW_HIT, 1, 1);
        } else {
		    player.sendMessage(getProperty("cooldown", true, new String[]{"<prefix>", GearzHub.getInstance().getChatPrefix()}));
	    }
    }

    public void handleToggle(Player player) {
        if (enabledFor.contains(player.getName())) {
	        for (Player online : Bukkit.getOnlinePlayers()) {
		        player.hidePlayer(online);
	        }
        } else {
	        for (Player online : Bukkit.getOnlinePlayers()) {
		        player.showPlayer(online);
	        }
        }
    }

    public boolean isEnabled(Player player) {
        return enabledFor.contains(player.getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	    //////////////////////////////////// GET RID OF OLD STAR /////////////////////////////////////////////

	    ItemStack itemStack = new ItemStack(Material.NETHER_STAR, 1);
	    ItemMeta meta = itemStack.getItemMeta();
	    meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Magic Star!" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Click me!");
	    itemStack.setItemMeta(meta);

	    if(event.getPlayer().getInventory().contains(itemStack)) event.getPlayer().getInventory().remove(itemStack);

	    /////////////////////////////////////////////////////////////////////////////////////////////////////

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isEnabled(player)) player.hidePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String user = player.getName();
        if (enabledFor.contains(user)) {
            enabledFor.remove(user);
        }
    }
}
