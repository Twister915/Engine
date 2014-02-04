package net.tbnr.gearz.effects.entityblocks;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public final class GearzBlockManager {

	private static List<GearzBlock> regBlocks = new ArrayList<>();

	public static GearzBlock registerBlock(Block b) {
		GearzBlock gearzBlock = getGearzBlock(b);
		if(gearzBlock != null) return gearzBlock;
		gearzBlock = new GearzBlock(b);
		regBlocks.add(gearzBlock);
		return gearzBlock;
	}

	public static boolean isRegistered(GearzBlock b) {
		return regBlocks.contains(b);
	}

	public static boolean isRegistered(Location l) {
		return getGearzBlock(l) != null;
	}

	private static GearzBlock getGearzBlock(Block b) {
		return getGearzBlock(b.getLocation());
	}

	private static GearzBlock getGearzBlock(Location l) {
		for(GearzBlock gearzBlock : regBlocks) {
			if(gearzBlock.getBlockX() != l.getBlockX()) continue;
			if(gearzBlock.getBlockY() != l.getBlockY()) continue;
			if(gearzBlock.getBlockZ() != l.getBlockZ()) continue;
			return gearzBlock;
		}
		return null;
	}
}
