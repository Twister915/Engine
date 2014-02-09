package net.tbnr.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.tbnr.gearz.Gearz;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rigor789 on 2013.12.23..
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class InventoryGUI implements Listener {
    @Getter
    /**
     * And ArrayList of all the items displayed on the GUI
     */
    private final ArrayList<InventoryGUIItem> items;
    @Getter
    /**
     * The title of the GUI
     */
    private final String title;
    @Getter
    /**
     * The callback of the GUI
     */
    private final InventoryGUICallback callback;
    @Getter
    /**
     * The inventory which is used to display the GUI
     */
    private Inventory inventory;

    /**
     * Whether the GUI should apply effects to the player when opened.
     */
    private final boolean effects;

    /**
     * An InventoryGUI with callbacks and effects on
     *
     * @param items    And array list of the items to be put in the GUI
     * @param title    The title of the GUI
     * @param callback The callback that handles the clicks.
     */
    public InventoryGUI(ArrayList<InventoryGUIItem> items, String title, InventoryGUICallback callback) {
        this(items, title, callback, true);
    }

    /**
     * An InventoryGUI with callbacks
     *
     * @param items    And array list of the items to be put in the GUI
     * @param title    The title of the GUI
     * @param callback The callback that handles the clicks.
     * @param effects  Whether to show or not the effects
     */
    public InventoryGUI(ArrayList<InventoryGUIItem> items, String title, InventoryGUICallback callback, boolean effects) {
        this.items = items;
        this.title = title;
        this.callback = callback;
        this.inventory = Bukkit.createInventory(null, determineSize(), title);
        updateContents(items);
        this.effects = effects;
		Bukkit.getServer().getPluginManager().registerEvents(this, Gearz.getInstance());
    }

    /**
     * Updates the items in the inventory
     *
     * @param items the items to update
     */
    public void updateContents(ArrayList<InventoryGUIItem> items) {
        inventory.clear();
        if (items == null) {
            return;
		}
		if(this.items.size() != items.size()) updateSize();
        for (int i = 0; i < items.size(); i++) {
            InventoryGUIItem item = items.get(i);
            if (item == null) continue;
            item.setSlot(i);
            inventory.setItem(i, item.getItem());
        }
    }

    private int determineSize() {
        int rowSize = 9;
        if (items == null || items.size() == 0) {
            return rowSize;
        }
        float i = items.size() % rowSize; //Remainder of items over 9
        float v = i / rowSize; //Convert to a decimal
        return (int) (Math.floor(items.size() / rowSize) /* Gives how many rows we need */ + Math.ceil(v) /* If we need an extra row */) * rowSize /*Times number of items per row */;
    }

    /**
     * Updates the size of the GUI
     */
    public void updateSize(){
        if(this.inventory.getSize() == determineSize()) return;
        this.inventory = Bukkit.createInventory(null, determineSize(), title);
        updateContents(items);
    }

    /**
     * Opens the GUI for @player
     */
    public void open(Player player) {
        player.openInventory(inventory);
        if (effects) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 128, false));
        }
        callback.onGUIOpen(this, player);
    }

    /**
     * Closes the GUI for @player
     */
    public void close(Player player) {
        if (effects) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        player.closeInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (!event.getInventory().getTitle().equalsIgnoreCase(this.inventory.getTitle())) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (effects) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        callback.onGUIClose(this, player);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!event.getInventory().getTitle().equalsIgnoreCase(this.inventory.getTitle())) {
            return;
        }
		event.setCancelled(true);
        Player player1 = (Player) event.getWhoClicked();
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
                break;
        }
        if (!cont) {
            return;
        }
        for (InventoryGUIItem item : this.items) {
            if (item == null ||
                    event.getCurrentItem() == null ||
                    !(event.getCurrentItem().equals(item.getItem()))) {
                continue;
            }
            this.callback.onItemSelect(this, item, player1);
        }
    }

    @ToString
    public static class InventoryGUIItem {
        @Getter
        /**
         * The ItemStack of the item
         */
        private final ItemStack item;
        @Getter
        /**
         * The name of the item (No chatcolors)
         */
        private final String name;

        @Getter @Setter
        /**
         * Slot the item is in
         */
        private int slot;

        /**
         * An item without lore
         *
         * @param item  the {@link org.bukkit.inventory.ItemStack} to use
         * @param name the name of the item
         */
        public InventoryGUIItem(ItemStack item, String name) {
            this(item, name, null);
        }

        /**
         * An item to be displayed in the GUI
         *
         * @param item the ItemStack
         * @param name the Name to be displayed
         * @param lore the lore to be displayed
         */
        public InventoryGUIItem(ItemStack item, String name, List<String> lore) {
            this.item = item;
            this.name = ChatColor.stripColor(name);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
    }

    public interface InventoryGUICallback {
        /**
         * Called when something has been pressed in the inventory GUI
         *
         * @param item   is the item that was pressed
         * @param player is the player who pressed it
         */
        public void onItemSelect(InventoryGUI gui, InventoryGUIItem item, Player player);

        /**
         * Called when the inventory is opened
         *
         * @param gui    is the gui that was opened
         * @param player is the player for whi the GUI was opened
         */
        public void onGUIOpen(InventoryGUI gui, Player player);

        /**
         * Called when the inventory is closed
         *
         * @param gui    is the gui that was closed
         * @param player is the player for who the GUI was closed
         */
        public void onGUIClose(InventoryGUI gui, Player player);
    }
}
