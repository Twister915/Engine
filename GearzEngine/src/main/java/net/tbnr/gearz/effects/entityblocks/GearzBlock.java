package net.tbnr.gearz.effects.entityblocks;

import lombok.Data;
import net.tbnr.gearz.effects.entityblocks.exceptions.GearzBlockException;
import org.bukkit.block.Block;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File: To provide a wrapper for bukkit block's with more method's
 * <p/>
 * Latest Change: created it and added base functions
 */
@Data public class GearzBlock {

	private double x;

	private double y;

	private double z;

	GearzBlock(Block block)	{
		GearzBlockManager.registerBlock(block);

	}

	public static GearzBlock block2GearzBlock(Block block) {
		return GearzBlockManager.registerBlock(block);
	}

	public int getBlockX() {
		return (int) Math.floor(x);
	}

	public int getBlockY() {
		return (int) Math.floor(x);
	}

	public int getBlockZ() {
		return (int) Math.floor(x);
	}

}
