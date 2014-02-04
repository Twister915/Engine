package net.tbnr.gearz.effects.entityblocks;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class EntityBlock extends GearzBlock {

	private final UUID UUID;

	EntityBlock(Location location) {
		super(location);
		this.UUID = spawnMinecart();
		giveNBTData();
	}

	public UUID spawnMinecart() {
		return block.getWorld().spawnEntity(block.getLocation(), EntityType.MINECART).getUniqueId();
	}

	public void giveNBTData() {
		//INSERT NBT EDITING HERE :) :) :)
	}

}
