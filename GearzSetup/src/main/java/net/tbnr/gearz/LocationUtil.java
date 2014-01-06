package net.tbnr.gearz;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by Joey on 12/19/13.
 */
public class LocationUtil {
    public static Location getMinimum(Location l1, Location l2) {
        Vector v1 = new Vector(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ());
        Vector v2 = new Vector(l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
        Vector minimum = Vector.getMinimum(v1, v2);
        return new Location(l1.getWorld(), minimum.getBlockX(), minimum.getBlockY(), minimum.getBlockZ());
    }

    public static Location getMaximum(Location l1, Location l2) {
        Vector v1 = new Vector(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ());
        Vector v2 = new Vector(l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
        Vector minimum = Vector.getMaximum(v1, v2);
        return new Location(l1.getWorld(), minimum.getBlockX(), minimum.getBlockY(), minimum.getBlockZ());
    }
}
