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

package net.tbnr.util.coloring;

import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.Map;

/**
 * Created by jake on 12/27/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ColorMap {

    private static final Map<DyeColor, ChatColor> chatColorMap = ImmutableMap.<DyeColor, ChatColor>builder().put(DyeColor.BLACK, ChatColor.BLACK).put(DyeColor.BLUE, ChatColor.DARK_BLUE).put(DyeColor.GREEN, ChatColor.DARK_GREEN).put(DyeColor.CYAN, ChatColor.DARK_AQUA).put(DyeColor.RED, ChatColor.RED).put(DyeColor.PURPLE, ChatColor.DARK_PURPLE).put(DyeColor.ORANGE, ChatColor.GOLD).put(DyeColor.SILVER, ChatColor.GRAY).put(DyeColor.GRAY, ChatColor.DARK_GRAY).put(DyeColor.LIGHT_BLUE, ChatColor.BLUE).put(DyeColor.LIME, ChatColor.GREEN).put(DyeColor.BROWN, ChatColor.DARK_RED).put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE).put(DyeColor.YELLOW, ChatColor.YELLOW).put(DyeColor.WHITE, ChatColor.WHITE).put(DyeColor.PINK, ChatColor.LIGHT_PURPLE).build();

    private static final Map<ChatColor, DyeColor> dyeColorMap = ImmutableMap.<ChatColor, DyeColor>builder().put(ChatColor.AQUA, DyeColor.LIGHT_BLUE).put(ChatColor.BLACK, DyeColor.BLACK).put(ChatColor.BLUE, DyeColor.BLUE).put(ChatColor.DARK_AQUA, DyeColor.CYAN).put(ChatColor.DARK_BLUE, DyeColor.BLUE).put(ChatColor.DARK_GRAY, DyeColor.GRAY).put(ChatColor.DARK_GREEN, DyeColor.GREEN).put(ChatColor.DARK_PURPLE, DyeColor.PURPLE).put(ChatColor.DARK_RED, DyeColor.RED).put(ChatColor.GOLD, DyeColor.ORANGE).put(ChatColor.GRAY, DyeColor.SILVER).put(ChatColor.GREEN, DyeColor.LIME).put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA).put(ChatColor.RED, DyeColor.RED).put(ChatColor.WHITE, DyeColor.WHITE).put(ChatColor.YELLOW, DyeColor.YELLOW).build();
}
