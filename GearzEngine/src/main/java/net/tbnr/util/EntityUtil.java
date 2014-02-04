package net.tbnr.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class EntityUtil {

	public static Entity UUID2Entity(UUID uuid) {
		for(World world : Bukkit.getServer().getWorlds()) {
			for(Entity e : world.getEntities()) {
				if(e.getUniqueId() == uuid) return e;
			}
		}
		return null;
	}

	public static Entity UUID2Entity(UUID uuid, World world) {
		for(Entity e : world.getEntities()) {
			if(e.getUniqueId() == uuid) return e;
		}
		return null;
	}
}
