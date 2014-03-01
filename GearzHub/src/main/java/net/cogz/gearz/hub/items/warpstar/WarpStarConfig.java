package net.cogz.gearz.hub.items.warpstar;

import net.cogz.gearz.hub.GearzHub;
import net.tbnr.util.inventory.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by rigor789 on 2013.12.23..
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class WarpStarConfig {

    private ArrayList<InventoryGUI.InventoryGUIItem> warps;
    private HashMap<String, Location> locations;

    public WarpStarConfig() {
        refresh();
    }

    public void refresh() {
        warps = new ArrayList<>();
        locations = new HashMap<>();
        ConfigurationSection section = GearzHub.getInstance().getConfig().getConfigurationSection("hub.warps");
        if (section == null) {
            return;
        }
        Set<String> keys = section.getKeys(false);
        for (String key : keys) {
            ConfigurationSection forKey = section.getConfigurationSection(key);
            String name = ChatColor.translateAlternateColorCodes('&', forKey.getString("name"));

            List<String> lore = forKey.getStringList("lore");
            for (String s : lore) {
                lore.set(lore.indexOf(s), ChatColor.translateAlternateColorCodes('&', s));
            }

            warps.add(new InventoryGUI.InventoryGUIItem(getItem(forKey.getString("item")), name, lore));
            locations.put(key.toLowerCase(), getLocation(forKey.getConfigurationSection("location")));
        }
    }

    private Location getLocation(ConfigurationSection section) {
        if (section == null) return null;
        return new Location(Bukkit.getWorld(section.getString("world")), section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
    }

    private ItemStack getItem(String name) {
        Material material;
        try {
            material = Material.getMaterial(name.toUpperCase());
        } catch (Exception e) {
            material = Material.GRASS;
        }
        return new ItemStack(material);
    }

    public Location getLocation(String forWarp) {
        if (locations.containsKey(forWarp.toLowerCase())) return locations.get(forWarp.toLowerCase());
        return null;
    }

    public ArrayList<InventoryGUI.InventoryGUIItem> getWarps() {
        return warps;
    }

}
