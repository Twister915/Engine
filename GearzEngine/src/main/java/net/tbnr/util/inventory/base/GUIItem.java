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

package net.tbnr.util.inventory.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/10/2014
 */
@ToString
public class GUIItem {

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
     * @param item the {@link org.bukkit.inventory.ItemStack} to use
     * @param name the name of the item
     */
    public GUIItem(ItemStack item, String name) {
        this(item, name, null);
    }

    /**
     * An item to be displayed in the GUI
     *
     * @param item the ItemStack
     * @param name the Name to be displayed
     * @param lore the lore to be displayed
     */
    public GUIItem(ItemStack item, String name, List<String> lore) {
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
        wrapLore(15);
        item.setItemMeta(meta);
    }

    /**
     * Wraps each String in item lore to a specific line length
     *
     * @param length the maximum line length
     */
    public void wrapLore(int length) {
        List<String> newLore = new ArrayList<>();
        if (!item.hasItemMeta() || item.getItemMeta().getLore() == null) return;
        for (String unwrapped : item.getItemMeta().getLore()) {
            newLore.addAll(Arrays.asList(ChatPaginator.wordWrap(unwrapped, length)));
        }
        item.getItemMeta().setLore(newLore);
    }
}
