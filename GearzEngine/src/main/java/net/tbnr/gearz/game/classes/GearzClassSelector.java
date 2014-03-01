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

package net.tbnr.gearz.game.classes;

import lombok.Getter;
import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Class Selector
 */
public final class GearzClassSelector implements InventoryGUI.InventoryGUICallback {
    @Getter
    private final List<GearzClass> classes = new ArrayList<>();
    private HashMap<String, GearzClass> currentClasses = new HashMap<>();
    private HashSet<GearzPlayer> needsNewClass = new HashSet<>();
    private InventoryGUI inventoryGUI;

    public static String getFileData(String filename, GearzPlugin plugin) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(plugin.getResource(filename)));
        String line;
        StringBuilder builder = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return builder.toString();
    }

    public static JSONObject getJSONResource(String resource, GearzPlugin plugin) {
        JSONObject object;
        try {
            object = new JSONObject(getFileData(resource, plugin));
        } catch (JSONException e) {
            return null;
        }
        return object;
    }

    public static GearzClassSelector selectorFromFiles(GearzPlugin plugin, String... files) {
        GearzClassSelector gearzClassSelector = new GearzClassSelector();
        for (String f : files) {
            try {
                gearzClassSelector.classes.add(GearzClass.classFromJsonObject(getJSONResource(f, plugin)));
            } catch (GearzClassReadException e) {
                plugin.getLogger().severe(e.getMessage() + " : JSON Exception causing load error for " + f);
                e.getJsonException().printStackTrace();
            }
        }
        return gearzClassSelector;
    }

    @Override
    public void onItemSelect(InventoryGUI gui, InventoryGUI.InventoryGUIItem item, Player player) {

    }

    @Override
    public void onGUIOpen(InventoryGUI gui, Player player) {

    }

    @Override
    public void onGUIClose(InventoryGUI gui, Player player) {

    }

    public void randomClassForPlayer(GearzPlayer player) {
        player.getTPlayer().resetPlayer();

    }

    public static void giveClassToPlayer(GearzPlayer player, GearzClass clazz) {
        Player player1 = player.getPlayer();
        if (clazz.getArmour() != null) {
            GearzItem[] gearzItems = clazz.getArmour().toArray(new GearzItem[4]);
            ItemStack[] itemStacks = new ItemStack[4];
            for (int x = 0; x < 4; x++) {
                if (gearzItems.length <= 3 - x) {
                    continue;
                }
                //ItemStack itemStack = gearzItems[3 - x].getItemStack();
                GearzItem item = gearzItems[3 - x];
                if (item == null) {
                    continue;
                }
                ItemStack itemStack = item.getItemStack();
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                itemStacks[x] = itemStack;
            }
            player1.getInventory().setArmorContents(itemStacks);
        }
        for (GearzItem gearzItem : clazz.getItems()) {
            if (gearzItem.getSlot() != null) {
                player1.getInventory().setItem(gearzItem.getSlot(), gearzItem.getItemStack());
            } else {
                player.getPlayer().getInventory().setItem(player.getPlayer().getInventory().firstEmpty(), gearzItem.getItemStack());
            }
        }
        if (clazz.getStatusEffects() != null) {
            for (GearzStatusEffect statusEffect : clazz.getStatusEffects()) {
                player.getPlayer().addPotionEffect(statusEffect.getPotionEffect());
            }
        }
    }
}
