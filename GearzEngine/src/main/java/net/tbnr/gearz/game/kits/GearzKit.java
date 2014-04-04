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

package net.tbnr.gearz.game.kits;

import lombok.*;
import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an ingame class
 */
@Data
@AllArgsConstructor
public final class GearzKit {
    @Setter(AccessLevel.PACKAGE) private List<GearzKitItem> armour;
    @Setter(AccessLevel.PACKAGE) @NonNull private List<GearzKitItem> items;
    @Setter(AccessLevel.PACKAGE) private List<GearzKitStatusEffect> statusEffects;

    public static GearzKit classFromJsonObject(JSONObject object) throws GearzKitReadException {
        JSONArray items;
        try {
            items = object.getJSONArray("items");
        } catch (JSONException e) {
            throw exceptionFromJSON("Missing critical field in class definition", e);
        }
        JSONArray armour = null;
        try {
            armour = object.getJSONArray("armour");
        } catch (JSONException ignored) {
        }
        List<GearzKitItem> gearzKitItems = new ArrayList<>();
        List<GearzKitItem> gearzArmour = null;
        for (int x = 0; x < items.length(); x++) {
            JSONObject item;
            try {
                item = items.getJSONObject(x);
            } catch (JSONException e) {
                throw exceptionFromJSON("Invalid object specified for item at index " + x, e);
            }
            GearzKitItem gItem = GearzKitItem.fromJsonObject(item);
            gearzKitItems.add(gItem);
        }
        if (armour != null) {
            gearzArmour = new ArrayList<>();
            for (int x = 0; x < armour.length(); x++) {
                JSONObject item;
                try {
                    item = armour.getJSONObject(x);
                } catch (JSONException e) {
                    throw exceptionFromJSON("Invalid Armour Block defined at " + x, e);
                }
                GearzKitItem gItem = GearzKitItem.fromJsonObject(item);
                gearzArmour.add(gItem);
            }
        }
        JSONArray statusEfs = null;
        try {
            statusEfs = object.getJSONArray("status_effects");
        } catch (JSONException ignored) {
        }
        List<GearzKitStatusEffect> statusEffects = null;
        if (statusEfs != null) {
            statusEffects = new ArrayList<>();
            for (int x = 0; x < statusEfs.length(); x++) {
                JSONObject jsonObject;
                try {
                    jsonObject = statusEfs.getJSONObject(x);
                } catch (JSONException e) {
                    throw exceptionFromJSON("Error reading status effect at " + x, e);
                }
                statusEffects.add(GearzKitStatusEffect.fromJSONResource(jsonObject));
            }
        }
        return new GearzKit(gearzArmour, gearzKitItems, statusEffects);
    }

    static GearzKitReadException exceptionFromJSON(String reason, JSONException ex) {
        GearzKitReadException readException = new GearzKitReadException(reason);
        readException.setJsonException(ex);
        return readException;
    }

    public static void giveClassToPlayer(GearzPlayer player, GearzKit clazz) {
        Player player1 = player.getPlayer();
        if (clazz.getArmour() != null) {
            GearzKitItem[] gearzKitItems = clazz.getArmour().toArray(new GearzKitItem[4]);
            ItemStack[] itemStacks = new ItemStack[4];
            for (int x = 0; x < 4; x++) {
                if (gearzKitItems.length <= 3 - x) {
                    continue;
                }
                //ItemStack itemStack = gearzKitItems[3 - x].getItemStack();
                GearzKitItem item = gearzKitItems[3 - x];
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
        for (GearzKitItem gearzKitItem : clazz.getItems()) {
            if (gearzKitItem.getSlot() != null) {
                player1.getInventory().setItem(gearzKitItem.getSlot(), gearzKitItem.getItemStack());
            } else {
                player.getPlayer().getInventory().setItem(player.getPlayer().getInventory().firstEmpty(), gearzKitItem.getItemStack());
            }
        }
        if (clazz.getStatusEffects() != null) {
            for (GearzKitStatusEffect statusEffect : clazz.getStatusEffects()) {
                player.getPlayer().addPotionEffect(statusEffect.getPotionEffect());
            }
        }
    }
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

}
