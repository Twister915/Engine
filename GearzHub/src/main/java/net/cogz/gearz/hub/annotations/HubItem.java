package net.cogz.gearz.hub.annotations;

import lombok.NonNull;
import net.cogz.gearz.hub.GearzHub;
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
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public abstract class HubItem implements Listener {
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
        if (!(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR))
            return;
        if (event.getPlayer().getItemInHand() == null) return;
        if (!event.getPlayer().getItemInHand().hasItemMeta()) return;
        if (!(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(getItems().get(0).getItemMeta().getDisplayName())))
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
        return GearzHub.getInstance().getConfig().get("hub-items." + name.key() + ".properties." + property);
    }

    /**
     * Returns the configuration section
     *
     * @return Object ~ the configuration section
     */
    public final ConfigurationSection getConfigurationSection() {
        HubItemMeta name = getClass().getAnnotation(HubItemMeta.class);
        if (name == null) return null;
        return GearzHub.getInstance().getConfig().getConfigurationSection("hub-items." + name.key() + ".properties");
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
        return GearzHub.getInstance().getFormat("hub-items." + name.key() + ".properties." + property, prefix, replacements);
    }
}
