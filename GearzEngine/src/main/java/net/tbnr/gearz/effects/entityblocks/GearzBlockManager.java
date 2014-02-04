package net.tbnr.gearz.effects.entityblocks;

import net.tbnr.gearz.effects.entityblocks.exceptions.GearzBlockException;
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

	public static GearzBlock registerBlock(Block b) throws GearzBlockException {
		if(isRegistered(b.getLocation())) return getGearzBlock(b);
		GearzBlock gearzBlock = new GearzBlock(b);
		regBlocks.add(gearzBlock);
		return gearzBlock;
	}

	public static boolean isRegistered(GearzBlock b) {
		return regBlocks.contains(b);
	}

	public static boolean isRegistered(Location l) {
		for(GearzBlock gearzBlock : regBlocks) {
			if(gearzBlock.getBlockX() != l.getBlockX()) continue;
			if(gearzBlock.getBlockY() != l.getBlockY()) continue;
			if(gearzBlock.getBlockZ() != l.getBlockZ()) continue;
			return true;
		}
		return false;
	}

	private static GearzBlock getGearzBlock(Block b) {

	}
}
