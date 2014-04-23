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

package net.tbnr.util.inventory.base;

import lombok.Getter;
import net.tbnr.gearz.Gearz;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/10/2014
 */
public abstract class BaseGUI implements Listener {
    @Getter
    private final ArrayList<GUIItem> items;

    @Getter
    private final String title;

    @Getter
    private final GUICallback callback;

    @Getter
    private Inventory inventory;

    private final boolean effects;

    public BaseGUI(ArrayList<GUIItem> items, String title, GUICallback callback) {
        this(items, title, callback, true);
    }

    public BaseGUI(ArrayList<GUIItem> items, String title, GUICallback callback, boolean effects) {
        this.items = items;
        this.title = title;
        this.callback = callback;
        this.inventory = Bukkit.createInventory(null, determineSize(), title);
        updateContents(items);
        this.effects = effects;
        Bukkit.getServer().getPluginManager().registerEvents(this, Gearz.getInstance());
    }

    public void updateContents(ArrayList<GUIItem> items) {
        inventory.clear();
        if (items == null) {
            return;
        }
        if (this.items.size() != items.size()) updateSize();
        for (int i = 0; i < items.size(); i++) {
            GUIItem item = items.get(i);
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

    public void updateSize() {
        if (this.inventory.getSize() == determineSize()) return;
        this.inventory = Bukkit.createInventory(null, determineSize(), title);
        updateContents(items);
    }

    public void openGUI(Player player) {
    }

    public void open(Player player) {
        player.openInventory(inventory);
        if (effects) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 128, false));
        }
        callback.onGUIOpen(this, player);
    }

    public void closeGUI(Player player) {
    }

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
        for (GUIItem item : this.items) {
            if (item == null ||
                    event.getCurrentItem() == null ||
                    !(event.getCurrentItem().equals(item.getItem()))) {
                continue;
            }
            this.callback.onItemSelect(this, item, player1);
        }
    }
}
