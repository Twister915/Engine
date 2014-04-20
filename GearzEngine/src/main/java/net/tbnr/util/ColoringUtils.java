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

package net.tbnr.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rigor789 on 2014.01.23..
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ColoringUtils implements GUtility {

    public static ItemStack colorizeLeather(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        if (material == Material.LEATHER_BOOTS || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_HELMET) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack colorizeWool(DyeColor color){
        Wool wool = new Wool(color);
        return wool.toItemStack();
    }

    public static List<String> colorStringList(List<String> strings) {
        ArrayList<String> string = new ArrayList<>();
        for (String s : strings) {
            string.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return string;
    }
}
