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

package net.cogz.engine.hub.annotations;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import net.cogz.engine.hub.GearzHub;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by rigor789 on 2013.12.21..
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
@Log
public abstract class HubItem implements Listener {
    @Getter
    @Setter
    GearzHub instance;
    public abstract List<ItemStack> getItems();

    public void rightClicked(Player player) {
    }

    public void leftClicked(Player player) {
    }

    public HubItem(boolean interactable) {
        if (interactable) GearzHub.getInstance().registerEvents(this);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public final void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if (event.getAction() == Action.PHYSICAL ||
                itemStack == null ||
                itemStack.getType() == Material.AIR ||
                !itemStack.hasItemMeta() ||
                !itemStack.getItemMeta().hasDisplayName() ||
                !itemStack.getItemMeta().getDisplayName().equals(getItems().get(0).getItemMeta().getDisplayName()))
            return;
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                rightClicked(event.getPlayer());
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                leftClicked(event.getPlayer());
                break;
            default:
                return;
        }
        event.setCancelled(true);
    }

    /**
     * Get property ~ Other object aka boolean etc.
     *
     * @param property ~ The property to get
     * @return Object ~ the property ~ Object
     */
    public final Object getPropertyObject(@NonNull String property) {
        HubItemMeta name = getClass().getAnnotation(HubItemMeta.class);
        if (name == null) return "";
        return getInstance().getSubHub().getConfig().get("hub-items." + name.key() + ".properties." + property);
    }

    /**
     * Returns the configuration section
     *
     * @return Object ~ the configuration section
     */
    public final ConfigurationSection getConfigurationSection() {
        HubItemMeta name = getClass().getAnnotation(HubItemMeta.class);
        if (name == null) return null;
        return getInstance().getSubHub().getConfig().getConfigurationSection("hub-items." + name.key() + ".properties");
    }

    /**
     * Get property like getFormat though it gets off property part
     * aka instead of getFormat("jaffa.othercategory.gsdjsdgdg")
     * it will automatically go to ("hub-items.<youritem>.properties.<property>")
     *
     * @param property the property to get
     * @return String ~ The property
     * @see net.tbnr.util.TPlugin#getFormat(String)
     */
    public final String getProperty(@NonNull String property) {
        return getProperty(property, false, new String[]{});
    }

    public final String getProperty(@NonNull String property, @NonNull boolean prefix) {
        return getProperty(property, prefix, new String[]{});
    }

    public final String getProperty(@NonNull String property, @NonNull boolean prefix, String[]... replacements) {
        HubItemMeta name = getClass().getAnnotation(HubItemMeta.class);
        if (name == null) return "";
        return getInstance().getSubHub().getFormat("hub-items." + name.key() + ".properties." + property, prefix, replacements);
    }
}
